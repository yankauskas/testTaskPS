package org.yankauskas.pstest.presentation

import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.fee.FirstExchangesFreePromo

class Config {
    object Api {
        const val BASE_URL = "https://developers.paysera.com/tasks/api/"
        const val POLLING_INTERVAL = 5_0000L
    }

    object Wallet {
        val WALLET_SET = setOf("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD")
        val BASE_CURRENCY = Currency("EUR")
        val BASE_BALANCE = 1000.toBigDecimal()
        val RATE_PRECISION_SCALE = 6
        val AMOUNT_PRECISION_SCALE = 2
    }

    object Rate {
        val BASE_FEE_RATE = 0.007.toBigDecimal()
        val PROMO_FEE_RATE = 0.005.toBigDecimal()
        val PROMO_AMOUNT = 200.toBigDecimal()
        val FIRST_EXCHANGES_FREE = 5
        val EVERY_POSITION_FREE = 10
        val promoList = listOf(
            FirstExchangesFreePromo(FIRST_EXCHANGES_FREE),
//            BaseCurrencyAmountPromo(PROMO_AMOUNT),
//            EveryPositionPromo(EVERY_POSITION_FREE, PROMO_FEE_RATE)
        )

        val RATE_EXPIRATION_TIME = 5_0000L
    }
}
