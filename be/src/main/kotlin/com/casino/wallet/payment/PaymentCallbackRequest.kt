package com.casino.wallet.payment

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class PaymentCallbackRequest(

    @field:NotNull
    val depositId: UUID,

    @field:NotBlank
    val providerReference: String,

    @field:Min(1)
    val amountCents: Long
)