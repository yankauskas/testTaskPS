package org.yankauskas.pstest.presentation.di

import org.koin.dsl.module
import org.yankauskas.pstest.domain.usecase.PerformOperationUseCase
import org.yankauskas.pstest.domain.usecase.StartPollingUseCase
import org.yankauskas.pstest.domain.usecase.WatchOperationUseCase
import org.yankauskas.pstest.domain.usecase.WatchWalletsUseCase
import org.yankauskas.pstest.domain.util.ExchangeUtils
import org.yankauskas.pstest.presentation.Config

val domainModule = module {
    single {
        ExchangeUtils(
            ratePrecisionScale = Config.Wallet.RATE_PRECISION_SCALE,
            amountPrecisionScale = Config.Wallet.AMOUNT_PRECISION_SCALE,
            baseCurrency = Config.Wallet.BASE_CURRENCY,
            rateExpirationTime = Config.Rate.RATE_EXPIRATION_TIME,
            baseFeeRate = Config.Rate.BASE_FEE_RATE,
            feePromos = Config.Rate.promoList
        )
    }

    factory { PerformOperationUseCase(get(), get()) }
    factory { StartPollingUseCase(get()) }
    factory { WatchOperationUseCase(get(), get()) }
    factory { WatchWalletsUseCase(get()) }
}