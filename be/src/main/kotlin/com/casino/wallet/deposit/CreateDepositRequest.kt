package com.casino.wallet.deposit

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateDepositRequest(

    @field:NotNull
    @field:DecimalMin(value = "0.01")
    @field:Digits(integer = 17, fraction = 2)
    val amount: BigDecimal
)