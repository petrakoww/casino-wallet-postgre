package com.casino.wallet.betting

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class SettledBetResponse(
    val id: UUID,
    val stake: BigDecimal,
    val realStake: BigDecimal,
    val bonusStake: BigDecimal,

    val winAmount: BigDecimal,
    val realWin: BigDecimal,
    val bonusWin: BigDecimal,

    val status: BetStatus,
    val settledAt: Instant?
) {
    companion object {

        fun from(bet: Bet): SettledBetResponse {
            return SettledBetResponse(
                id = bet.id,
                stake = bet.stake,
                realStake = bet.realStake,
                bonusStake = bet.bonusStake,

                winAmount = bet.winAmount
                    ?: BigDecimal("0.00"),

                realWin = bet.realWin
                    ?: BigDecimal("0.00"),

                bonusWin = bet.bonusWin
                    ?: BigDecimal("0.00"),

                status = bet.status,
                settledAt = bet.settledAt
            )
        }
    }
}