package org.yankauskas.pstest.presentation.di

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import org.yankauskas.pstest.BuildConfig
import org.yankauskas.pstest.data.datasource.rates.RatesCloudDataSource
import org.yankauskas.pstest.data.datasource.rates.RatesDataSource
import org.yankauskas.pstest.data.entity.mapper.RatesSetMapper
import org.yankauskas.pstest.data.factory.WalletSetFactory
import org.yankauskas.pstest.data.net.RatesApiService
import org.yankauskas.pstest.data.repository.ExchangeRepositoryImpl
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.repository.ExchangeRepository
import org.yankauskas.pstest.presentation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

val dataModule = module {
    // Base
    single { provideBaseOkHttpClient() }
    single { provideBaseRetrofit(get()) }
    single { provideRatesApiService(get()) }

    // DataSource
    single<RatesDataSource> { RatesCloudDataSource(get()) }

    //Mapper

    factory { RatesSetMapper() }

    //Factory

    single<WalletSetFactory> {
        object : WalletSetFactory {
            override fun createWalletSet(): Map<Currency, BigDecimal> {
                return Config.Wallet.WALLET_SET.map { Currency(it) }.associateWith { BigDecimal.ZERO } +
                        (Config.Wallet.BASE_CURRENCY to Config.Wallet.BASE_BALANCE)
            }
        }
    }

    //Repository

    single<ExchangeRepository> { ExchangeRepositoryImpl(get(), get(), get(), Config.Api.POLLING_INTERVAL) }
}

private fun provideBaseOkHttpClient(): OkHttpClient {
    val okHttpBuilder = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })

    return okHttpBuilder.build()
}

private fun provideBaseRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .baseUrl(Config.Api.BASE_URL)
        .client(okHttpClient)
        .build()
}

private fun provideRatesApiService(retrofit: Retrofit): RatesApiService {
    return retrofit.create(RatesApiService::class.java)
}