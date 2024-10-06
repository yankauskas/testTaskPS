package org.yankauskas.pstest.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.RatesSet
import org.yankauskas.pstest.domain.model.Resource
import org.yankauskas.pstest.domain.model.Transaction
import java.math.BigDecimal

interface ExchangeRepository {
    val rates: StateFlow<RatesSet>
    val wallets: StateFlow<HashMap<Currency, BigDecimal>>
    val transactions: StateFlow<List<Transaction>>

    suspend fun startPollingRates(): Flow<Resource<Unit>>
    suspend fun performTransaction(transaction: Transaction)
}