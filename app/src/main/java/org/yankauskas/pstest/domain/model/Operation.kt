package org.yankauskas.pstest.domain.model

import java.math.BigDecimal

data class Operation(val from: Currency, val to: Currency, val amount: BigDecimal)