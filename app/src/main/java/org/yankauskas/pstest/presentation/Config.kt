package org.yankauskas.pstest.presentation

class Config {
    object Api {
        const val BASE_URL = "https://developers.paysera.com/tasks/api/"
        const val POLLING_INTERVAL = 5_0000L
    }

    object Wallet {
        val WALLET_SET = setOf("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD")
        val BASE_CURRENCY = "EUR"
        val BASE_BALANCE = 1000.toBigDecimal()
        val RATE_EXPIRATION_TIME = 5_0000L
        val RATE_PRECISION_SCALE = 6
        val AMOUNT_PRECISION_SCALE = 2
        val BASE_FEE_RATE = 0.007.toBigDecimal()
    }
}