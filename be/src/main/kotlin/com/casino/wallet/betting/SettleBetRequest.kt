package com.casino.wallet.betting

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class SettleBetRequest(
    @field:NotNull
    @field:DecimalMin(value = "0.00")
    @field:Digits(integer = 17, fraction = 2)
    val winAmount: BigDecimal
)