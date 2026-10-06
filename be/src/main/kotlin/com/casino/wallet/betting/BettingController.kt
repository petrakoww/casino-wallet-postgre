package com.casino.wallet.betting

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/players/{playerId}/bets")
class BettingController(
    private val bettingService: BettingService,
    private val betSettlementService: BetSettlementService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun placeBet(
        @PathVariable playerId: UUID,
        @Valid @RequestBody request: PlaceBetRequest
    ): BetResponse {

        return bettingService.placeBet(
            playerId = playerId,
            request = request
        )
    }

    @PostMapping("/{betId}/settle")
    fun settleBet(
        @PathVariable playerId: UUID,
        @PathVariable betId: UUID,
        @Valid @RequestBody request: SettleBetRequest
    ): SettledBetResponse {

        return betSettlementService.settle(
            playerId = playerId,
            betId = betId,
            request = request
        )
    }
}