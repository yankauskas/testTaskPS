package org.yankauskas.pstest.data.factory

import org.yankauskas.pstest.domain.model.Currency

interface WalletSetFactory {
    fun createWalletSet(): Set<Currency>
}