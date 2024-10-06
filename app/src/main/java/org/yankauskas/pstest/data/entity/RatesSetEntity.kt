package org.yankauskas.pstest.data.entity

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RatesSetEntity(
    @SerializedName("base") val base: String,
    @SerializedName("rates") val rates: Map<String, BigDecimal>
)