package org.yankauskas.pstest.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import org.yankauskas.pstest.domain.model.Operation
import org.yankauskas.pstest.domain.repository.ExchangeRepository
import org.yankauskas.pstest.domain.util.ExchangeUtils
import java.math.BigDecimal

class WatchOperationUseCase(private val repository: ExchangeRepository, private val exchangeUtils: ExchangeUtils) {

    operator fun invoke(operation: Operation): Flow<BigDecimal> = with(operation) {
        channelFlow { repository.rates.collectLatest { send(exchangeUtils.getExchangeAmount(amount, exchangeUtils.getRate(it, from, to))) } }
    }

}