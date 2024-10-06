package org.yankauskas.pstest.data.datasource.rates

import org.yankauskas.pstest.data.entity.RatesSetEntity
import org.yankauskas.pstest.domain.model.Resource

interface RatesDataSource {
    suspend fun getRates(): Resource<RatesSetEntity>
}