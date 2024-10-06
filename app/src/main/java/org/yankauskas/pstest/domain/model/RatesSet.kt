package org.yankauskas.pstest.domain.model

import java.math.BigDecimal
import java.util.Date

data class RatesSet(val date: Date, val baseCurrency: Currency, val rates: Map<Currency, BigDecimal>)