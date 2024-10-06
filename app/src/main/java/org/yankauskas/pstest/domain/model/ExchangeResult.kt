package org.yankauskas.pstest.domain.model

sealed class ExchangeResult {
    data class Success(val transaction: Transaction) : ExchangeResult()
    data class Error(val error: ExchangeError) : ExchangeResult()
}