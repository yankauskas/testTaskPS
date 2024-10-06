package org.yankauskas.pstest.presentation.di

import org.koin.dsl.module
import org.yankauskas.pstest.domain.usecase.PerformOperationUseCase
import org.yankauskas.pstest.domain.usecase.StartPollingUseCase
import org.yankauskas.pstest.domain.usecase.WatchOperationUseCase
import org.yankauskas.pstest.domain.usecase.WatchWalletsUseCase
import org.yankauskas.pstest.domain.util.ExchangeUtils
import org.yankauskas.pstest.presentation.Config

val domainModule = module {
    single { ExchangeUtils(Config.Wallet.RATE_PRECISION_SCALE, 0.01.toBigDecimal(), Config.Wallet.RATE_EXPIRATION_TIME) }

    factory { PerformOperationUseCase(get(), get()) }
    factory { StartPollingUseCase(get()) }
    factory { WatchOperationUseCase(get(), get()) }
    factory { WatchWalletsUseCase(get()) }
}