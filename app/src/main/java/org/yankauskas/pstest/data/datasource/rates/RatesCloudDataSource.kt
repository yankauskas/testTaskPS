package org.yankauskas.pstest.data.datasource.rates

import org.yankauskas.pstest.data.datasource.getNetworkResource
import org.yankauskas.pstest.data.entity.RatesSetEntity
import org.yankauskas.pstest.data.net.RatesApiService
import org.yankauskas.pstest.domain.model.Resource

class RatesCloudDataSource(private val ratesApiService: RatesApiService): RatesDataSource {
    override suspend fun getRates(): Resource<RatesSetEntity> = getNetworkResource { ratesApiService.getRates() }
}