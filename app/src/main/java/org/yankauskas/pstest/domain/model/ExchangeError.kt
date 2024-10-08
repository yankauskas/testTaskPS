package org.yankauskas.pstest.domain.model

import java.math.BigDecimal

sealed class ExchangeError {
    data class ZeroAmount(val currency: Currency) : ExchangeError()
    data object RateExpired : ExchangeError()
    data object SameCurrency : ExchangeError()
    data class NoRate(val fromCurrency: Currency, val toCurrency: Currency) : ExchangeError()
    data class InsufficientFunds(val fromCurrency: Currency, val amount: BigDecimal) : ExchangeError()
    data class NoSuchWallet(val currency: Currency) : ExchangeError()
    data class Unknown(val message: String) : ExchangeError()
}