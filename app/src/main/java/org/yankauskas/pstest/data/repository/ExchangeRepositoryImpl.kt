package org.yankauskas.pstest.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.yankauskas.pstest.data.datasource.rates.RatesDataSource
import org.yankauskas.pstest.data.entity.mapper.RatesSetMapper
import org.yankauskas.pstest.data.factory.WalletSetFactory
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.RatesSet
import org.yankauskas.pstest.domain.model.Resource
import org.yankauskas.pstest.domain.model.Transaction
import org.yankauskas.pstest.domain.model.doOnSuccess
import org.yankauskas.pstest.domain.model.omitSuccess
import org.yankauskas.pstest.domain.repository.ExchangeRepository
import java.math.BigDecimal

class ExchangeRepositoryImpl(
    private val ratesDataSource: RatesDataSource,
    private val ratesSetMapper: RatesSetMapper,
    private val walletSetFactory: WalletSetFactory,
    private val pollingInterval: Long
) : ExchangeRepository {

    private val _rates = MutableStateFlow(RatesSet.EMPTY)
    override val rates: StateFlow<RatesSet> = _rates.asStateFlow()

    private val _wallets =
        MutableStateFlow(walletSetFactory.createWalletSet().associateWith { BigDecimal.ZERO })
    override val wallets: StateFlow<Map<Currency, BigDecimal>> = _wallets.asStateFlow()

    private val _transactions = MutableStateFlow(emptyList<Transaction>())
    override val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()


    override fun startPollingRates(): Flow<Resource<Unit>> = flow {
        while (true) {
            val response = ratesDataSource.getRates()
            response.doOnSuccess { _rates.emit(ratesSetMapper.transform(it)) }
            emit(response.omitSuccess())
            delay(pollingInterval)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun performTransaction(transaction: Transaction) {
        val wallets = _wallets.value
        val fromWallet = wallets[transaction.fromCurrency]
        val toWallet = wallets[transaction.toCurrency]

        if (fromWallet == null || toWallet == null) {
            throw IllegalStateException("Wallets should not be null")
        }

        val fromAmount = transaction.fromAmount + transaction.fee

        val fromNew = fromWallet - fromAmount
        val toNew = toWallet + transaction.toAmount

        _wallets.emit(wallets + mapOf(transaction.fromCurrency to fromNew, transaction.toCurrency to toNew))
        _transactions.emit(_transactions.value + transaction)
    }
}