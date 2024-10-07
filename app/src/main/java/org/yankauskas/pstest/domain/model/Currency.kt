package org.yankauskas.pstest.domain.model

data class Currency(val code: String) {
    companion object {
        val EMPTY = Currency("")
    }
}