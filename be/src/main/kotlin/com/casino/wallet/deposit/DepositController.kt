package com.casino.wallet.deposit

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/players/{playerId}/deposits")
class DepositController(
    private val depositService: DepositService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createDeposit(
        @PathVariable playerId: UUID,
        @Valid @RequestBody request: CreateDepositRequest
    ): DepositResponse {

        return depositService.createDeposit(
            playerId = playerId,
            request = request
        )
    }
}