package org.yankauskas.pstest.domain.model.fee

import java.math.BigDecimal

class EveryPositionPromo(private val whichPosition: Int, private val promoRate: BigDecimal = BigDecimal.ZERO): FeePromo {
    override fun invoke(baseCurrencyAmount: BigDecimal, operationNumber: Int): BigDecimal {
        return if (operationNumber % whichPosition == 0) promoRate else BigDecimal.ONE
    }
}