package com.casino.wallet.player

import java.math.BigDecimal
import java.util.UUID

data class WalletResponse(
    val playerId: UUID,
    val username: String,
    val realBalance: BigDecimal,
    val bonusBalance: BigDecimal,
    val totalBalance: BigDecimal
) {
    companion object {
        fun from(player: Player): WalletResponse {
            return WalletResponse(
                playerId = player.id,
                username = player.username,
                realBalance = player.realBalance,
                bonusBalance = player.bonusBalance,
                totalBalance = player.realBalance.add(player.bonusBalance)
            )
        }
    }
}