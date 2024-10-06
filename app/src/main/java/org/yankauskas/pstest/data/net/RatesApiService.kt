package org.yankauskas.pstest.data.net

import org.yankauskas.pstest.data.entity.RatesSetEntity
import retrofit2.Response
import retrofit2.http.GET

interface RatesApiService {
    @GET("currency-exchange-rates")
    suspend fun getRates(): Response<RatesSetEntity>
}