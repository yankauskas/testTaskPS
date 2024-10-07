package org.yankauskas.pstest.presentation.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.yankauskas.pstest.presentation.exchange.ExchangeViewModel

val presentationModule = module {
    viewModel { ExchangeViewModel(get(), get(), get(), get()) }
}