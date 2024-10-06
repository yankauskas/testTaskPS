package org.yankauskas.pstest.data.entity.mapper

import org.yankauskas.pstest.data.entity.RatesSetEntity
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.RatesSet
import java.util.Date

class RatesSetMapper : EntityMapper<RatesSetEntity, RatesSet>() {
    override fun transform(entity: RatesSetEntity): RatesSet {
        val baseCurrency = Currency(entity.base)
        val rates = entity.rates.map { (code, rate) -> Currency(code) to rate }.toMap()
        return RatesSet(Date(), baseCurrency, rates)
    }
}