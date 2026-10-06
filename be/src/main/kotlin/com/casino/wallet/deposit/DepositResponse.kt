package com.casino.wallet.deposit

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DepositResponse(
    val id: UUID,
    val playerId: UUID,
    val amount: BigDecimal,
    val status: DepositStatus,
    val providerReference: String?,
    val createdAt: Instant,
    val completedAt: Instant?
) {
    companion object {
        fun from(deposit: Deposit): DepositResponse {
            return DepositResponse(
                id = deposit.id,
                playerId = deposit.player.id,
                amount = deposit.amount,
                status = deposit.status,
                providerReference = deposit.providerReference,
                createdAt = deposit.createdAt,
                completedAt = deposit.completedAt
            )
        }
    }
}