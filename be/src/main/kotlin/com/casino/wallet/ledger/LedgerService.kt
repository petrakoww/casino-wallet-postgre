package com.casino.wallet.ledger

import com.casino.wallet.player.Player
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class LedgerService(
    private val ledgerRepository: LedgerRepository
) {

    fun record(
        player: Player,
        type: LedgerEntryType,
        realChange: BigDecimal,
        bonusChange: BigDecimal,
        referenceType: String? = null,
        referenceId: UUID? = null,
        description: String? = null
    ): LedgerEntry {

        val entry = LedgerEntry(
            player = player,
            type = type,
            realChange = realChange,
            bonusChange = bonusChange,
            realBalanceAfter = player.realBalance,
            bonusBalanceAfter = player.bonusBalance,
            referenceType = referenceType,
            referenceId = referenceId,
            description = description
        )

        return ledgerRepository.save(entry)
    }

    fun getPage(
        playerId: UUID,
        page: Int,
        size: Int
    ): LedgerPageResponse {

        // pagination
        val safeSize =
            size.coerceIn(1, 100)

        val pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                safeSize,
                Sort.by(
                    Sort.Direction.DESC,
                    "createdAt"
                )
            )

        val result =
            ledgerRepository.findAllByPlayerId(
                playerId = playerId,
                pageable = pageable
            )

        return LedgerPageResponse(
            content = result.content.map {
                LedgerEntryResponse.from(it)
            },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            first = result.isFirst,
            last = result.isLast
        )
    }
}