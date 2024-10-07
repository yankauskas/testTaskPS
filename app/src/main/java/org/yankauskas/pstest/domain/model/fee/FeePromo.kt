package org.yankauskas.pstest.domain.model.fee

import java.math.BigDecimal

interface FeePromo {
    // Returns BigDecimal.ONE for no promo, otherwise returns promo rate
    operator fun invoke(baseCurrencyAmount: BigDecimal, operationNumber: Int): BigDecimal
}