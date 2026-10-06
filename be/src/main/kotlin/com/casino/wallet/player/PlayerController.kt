package com.casino.wallet.player

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/players")
class PlayerController(
    private val playerService: PlayerService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreatePlayerRequest
    ): PlayerResponse {

        return playerService.create(request)
    }

    @GetMapping("/{playerId}/wallet")
    fun getWallet(
        @PathVariable playerId: java.util.UUID
    ): WalletResponse {

        return playerService.getWallet(playerId)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginPlayerRequest
    ): PlayerResponse {

        return playerService.loginOrCreate(
            request
        )
    }
}