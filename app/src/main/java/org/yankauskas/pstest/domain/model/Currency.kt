package org.yankauskas.pstest.domain.model

enum class Currency(val code: String) {
    EUR("EUR"),
    USD("USD"),
    GBP("GBP"),
    UAH("UAH"),
    CHF("CHF"),
    UNDEFINED("");

    companion object {
        fun fromCode(code: String): Currency = entries.firstOrNull { it.code == code } ?: UNDEFINED
    }
}