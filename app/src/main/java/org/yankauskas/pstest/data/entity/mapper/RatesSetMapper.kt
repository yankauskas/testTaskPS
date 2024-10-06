package org.yankauskas.pstest.data.entity.mapper

import org.yankauskas.pstest.data.entity.RatesSetEntity
import org.yankauskas.pstest.domain.model.Currency
import org.yankauskas.pstest.domain.model.RatesSet
import java.util.Date

class RatesSetMapper : EntityMapper<RatesSetEntity, RatesSet>() {
    override fun transform(entity: RatesSetEntity): RatesSet {
        val baseCurrency = Currency fromCode entity.base
        val rates =
            entity.rates.filterNot { Currency fromCode it.key == Currency.UNDEFINED }.map { (code, rate) -> Currency fromCode code to rate }
                .toMap()
        return RatesSet(Date(), baseCurrency, rates)
    }
}