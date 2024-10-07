package org.yankauskas.pstest.domain.model.fee

import java.math.BigDecimal

class FirstExchangesFreePromo(private val freeExchangesNumber: Int): FeePromo {
    override fun invoke(baseCurrencyAmount: BigDecimal, operationNumber: Int): BigDecimal {
        return if (operationNumber <= freeExchangesNumber) BigDecimal.ZERO else BigDecimal.ONE
    }
}