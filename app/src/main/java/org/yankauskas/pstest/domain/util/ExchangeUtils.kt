package org.yankauskas.pstest.domain.util

import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.Operation
import org.yankauskas.pstest.domain.model.RatesSet
import java.math.BigDecimal
import java.math.RoundingMode

class ExchangeUtils(
    private val precisionScale: Int,
    private val baseFeeRate: BigDecimal,
    private val rateExpirationTime: Long
) {
    fun getRate(ratesSet: RatesSet, from: Currency, to: Currency): BigDecimal {
        when {
            from == to -> return BigDecimal.ONE
            from == ratesSet.baseCurrency -> return ratesSet.rates[to] ?: BigDecimal.ZERO
            to == ratesSet.baseCurrency -> return ratesSet.rates[from]?.let { BigDecimal.ONE.divide(it, precisionScale, RoundingMode.DOWN) }
                ?: BigDecimal.ZERO

            else -> {
                val fromRate = getRate(ratesSet, from, ratesSet.baseCurrency)
                val toRate = getRate(ratesSet, ratesSet.baseCurrency, to)
                return toRate.multiply(fromRate)
            }
        }
    }

    fun getFee(operation: Operation, transactionNumber: Int): BigDecimal {
        val feeRate = baseFeeRate
        return operation.amount.multiply(feeRate)
    }

    fun isRateExpired(ratesSet: RatesSet): Boolean {
        return System.currentTimeMillis() - ratesSet.date.time > rateExpirationTime
    }
}