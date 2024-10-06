package org.yankauskas.pstest.domain.usecase

import org.yankauskas.pstest.domain.repository.ExchangeRepository

class WatchWalletsUseCase(private val repository: ExchangeRepository) {
    operator fun invoke() = repository.wallets
}