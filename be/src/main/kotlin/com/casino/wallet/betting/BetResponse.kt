package com.casino.wallet.betting

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BetResponse(
    val id: UUID,
    val playerId: UUID,
    val stake: BigDecimal,
    val realStake: BigDecimal,
    val bonusStake: BigDecimal,
    val status: BetStatus,
    val createdAt: Instant
) {
    companion object {
        fun from(bet: Bet): BetResponse {
            return BetResponse(
                id = bet.id,
                playerId = bet.player.id,
                stake = bet.stake,
                realStake = bet.realStake,
                bonusStake = bet.bonusStake,
                status = bet.status,
                createdAt = bet.createdAt
            )
        }
    }
}