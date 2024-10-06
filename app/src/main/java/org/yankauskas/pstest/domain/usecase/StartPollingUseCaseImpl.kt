package org.yankauskas.pstest.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.yankauskas.pstest.domain.model.Resource
import org.yankauskas.pstest.domain.repository.ExchangeRepository


class StartPollingUseCaseImpl(private val repository: ExchangeRepository) {
    suspend operator fun invoke(): Flow<Resource<Unit>> = repository.startPollingRates()
}