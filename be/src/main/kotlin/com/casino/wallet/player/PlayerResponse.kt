package com.casino.wallet.player

import java.math.BigDecimal
import java.util.UUID

data class PlayerResponse(
    val id: UUID,
    val username: String,
    val realBalance: BigDecimal,
    val bonusBalance: BigDecimal
) {
    companion object {
        fun from(player: Player): PlayerResponse {
            return PlayerResponse(
                id = player.id,
                username = player.username,
                realBalance = player.realBalance,
                bonusBalance = player.bonusBalance
            )
        }
    }
}