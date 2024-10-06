package org.yankauskas.pstest.domain.model

import java.math.BigDecimal
import java.util.Date

data class Transaction(
    val date: Date,
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val fromAmount: BigDecimal,
    val toAmount: BigDecimal,
    val fee: BigDecimal
)