package org.yankauskas.pstest.domain.usecase

import org.yankauskas.pstest.domain.model.ExchangeError
import org.yankauskas.pstest.domain.model.ExchangeResult
import org.yankauskas.pstest.domain.model.Operation
import org.yankauskas.pstest.domain.model.Transaction
import org.yankauskas.pstest.domain.repository.ExchangeRepository
import org.yankauskas.pstest.domain.util.ExchangeUtils
import java.math.BigDecimal
import java.util.Date

class PerformOperationUseCase(
    private val repository: ExchangeRepository,
    private val exchangeUtils: ExchangeUtils
) {
    suspend operator fun invoke(operation: Operation): ExchangeResult {
        if (operation.amount <= BigDecimal.ZERO) return ExchangeResult.Error(ExchangeError.ZeroAmount(operation.from))
        val rates = repository.rates.value
        val wallets = repository.wallets.value
        val fromWallet =
            wallets.getOrDefault(operation.from, null) ?: return ExchangeResult.Error(ExchangeError.NoSuchWallet(operation.from))
        wallets.getOrDefault(operation.to, null) ?: return ExchangeResult.Error(ExchangeError.NoSuchWallet(operation.to))
        val rate = exchangeUtils.getRate(rates, operation.from, operation.to)
        val transactionsNumber = repository.transactions.value.size + 1
        val fee = exchangeUtils.getFee(rates, operation, transactionsNumber)
        val fromAmount = operation.amount + fee

        if (operation.from == operation.to) return ExchangeResult.Error(ExchangeError.SameCurrency)
        if (exchangeUtils.isRateExpired(rates)) return ExchangeResult.Error(ExchangeError.RateExpired)
        if (rate == BigDecimal.ZERO) return ExchangeResult.Error(ExchangeError.NoRate(operation.from, operation.to))
        if (fromWallet < fromAmount) return ExchangeResult.Error(ExchangeError.InsufficientFunds(operation.from, fromAmount))

        val toAmount = exchangeUtils.getExchangeAmount(operation.amount, rate)

        val transaction = Transaction(Date(), operation.from, operation.to, operation.amount, toAmount, fee)
        try {
            repository.performTransaction(transaction)
        } catch (e: IllegalStateException) {
            return ExchangeResult.Error(ExchangeError.Unknown(e.message ?: "Unknown error"))
        }

        return ExchangeResult.Success(transaction)
    }
}