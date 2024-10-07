package org.yankauskas.pstest.domain.util

import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.Operation
import org.yankauskas.pstest.domain.model.RatesSet
import org.yankauskas.pstest.domain.model.fee.FeePromo
import java.math.BigDecimal
import java.math.RoundingMode

class ExchangeUtils(
    private val ratePrecisionScale: Int,
    private val amountPrecisionScale: Int,
    private val baseCurrency: Currency,
    private val rateExpirationTime: Long,
    private val baseFeeRate: BigDecimal,
    private val feePromos: List<FeePromo>
) {
    fun getRate(ratesSet: RatesSet, from: Currency, to: Currency): BigDecimal {
        when {
            from == to -> return BigDecimal.ONE
            from == ratesSet.baseCurrency -> return ratesSet.rates[to] ?: BigDecimal.ZERO
            to == ratesSet.baseCurrency ->
                return ratesSet.rates[from]?.let { BigDecimal.ONE.divide(it, ratePrecisionScale, RoundingMode.DOWN) }
                    ?: BigDecimal.ZERO

            else -> {
                val fromRate = getRate(ratesSet, from, ratesSet.baseCurrency)
                val toRate = getRate(ratesSet, ratesSet.baseCurrency, to)
                return toRate.multiply(fromRate)
            }
        }
    }

    fun getFee(ratesSet: RatesSet, operation: Operation, transactionNumber: Int): BigDecimal {
        val baseCurrencyAmount = getExchangeAmount(operation.amount, getRate(ratesSet, operation.from, baseCurrency))
        val feeRate = minOf(feePromos.minOf { it(baseCurrencyAmount, transactionNumber) }, baseFeeRate)
        return operation.amount.multiply(feeRate).setScale(amountPrecisionScale, RoundingMode.UP)
    }

    fun isRateExpired(ratesSet: RatesSet): Boolean {
        return System.currentTimeMillis() - ratesSet.date.time > rateExpirationTime
    }

    fun getExchangeAmount(amount: BigDecimal, rate: BigDecimal): BigDecimal {
        return amount.multiply(rate).setScale(amountPrecisionScale, RoundingMode.DOWN)
    }
}