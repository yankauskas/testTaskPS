package org.yankauskas.pstest.domain.model.fee

import java.math.BigDecimal

class BaseCurrencyAmountPromo(private val minAmount: BigDecimal, private val promoRate: BigDecimal = BigDecimal.ZERO): FeePromo {
    override fun invoke(baseCurrencyAmount: BigDecimal, operationNumber: Int): BigDecimal {
        return if (baseCurrencyAmount >= minAmount) promoRate else BigDecimal.ONE
    }
}