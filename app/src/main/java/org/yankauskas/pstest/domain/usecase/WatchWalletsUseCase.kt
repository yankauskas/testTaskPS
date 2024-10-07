package org.yankauskas.pstest.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.repository.ExchangeRepository
import java.math.BigDecimal

class WatchWalletsUseCase(private val repository: ExchangeRepository) {
    operator fun invoke(): StateFlow<Map<Currency, BigDecimal>> = repository.wallets
}