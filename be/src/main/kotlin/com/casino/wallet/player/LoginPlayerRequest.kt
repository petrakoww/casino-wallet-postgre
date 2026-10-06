package com.casino.wallet.player

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginPlayerRequest(
    @field:NotBlank
    @field:Size(
        min = 2,
        max = 100
    )
    val username: String
)
