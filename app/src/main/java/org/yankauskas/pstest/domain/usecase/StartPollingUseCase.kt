package org.yankauskas.pstest.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.yankauskas.pstest.domain.model.Resource
import org.yankauskas.pstest.domain.repository.ExchangeRepository


class StartPollingUseCase(private val repository: ExchangeRepository) {
    operator fun invoke(): Flow<Resource<Unit>> = repository.startPollingRates()
}