package com.casino.wallet.player

import jakarta.validation.constraints.NotBlank

data class CreatePlayerRequest(

    @field:NotBlank
    val username: String
)