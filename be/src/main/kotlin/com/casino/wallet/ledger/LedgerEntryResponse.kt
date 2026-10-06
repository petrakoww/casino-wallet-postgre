package com.casino.wallet.ledger

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class LedgerEntryResponse(
    val id: UUID,
    val type: LedgerEntryType,
    val realChange: BigDecimal,
    val bonusChange: BigDecimal,
    val realBalanceAfter: BigDecimal,
    val bonusBalanceAfter: BigDecimal,
    val referenceType: String?,
    val referenceId: UUID?,
    val description: String?,
    val createdAt: Instant
) {
    companion object {
        fun from(entry: LedgerEntry): LedgerEntryResponse {
            return LedgerEntryResponse(
                id = entry.id,
                type = entry.type,
                realChange = entry.realChange,
                bonusChange = entry.bonusChange,
                realBalanceAfter = entry.realBalanceAfter,
                bonusBalanceAfter = entry.bonusBalanceAfter,
                referenceType = entry.referenceType,
                referenceId = entry.referenceId,
                description = entry.description,
                createdAt = entry.createdAt
            )
        }
    }
}