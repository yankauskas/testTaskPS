package org.yankauskas.pstest.data.factory

import org.yankauskas.pstest.domain.model.Currency
import java.math.BigDecimal

interface WalletSetFactory {
    fun createWalletSet(): Map<Currency, BigDecimal>
}