package com.casino.wallet.bonus

import com.casino.wallet.ledger.LedgerEntryType
import com.casino.wallet.ledger.LedgerService
import com.casino.wallet.player.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class BonusExpirationService(
    private val playerRepository: PlayerRepository,
    private val bonusRepository: BonusRepository,
    private val ledgerService: LedgerService
) {

    @Transactional(
        propagation = Propagation.REQUIRES_NEW
    )
    fun expireIfNeeded(
        playerId: UUID
    ) {

        // create new transaction to avoid rolling back the expiration if the bet fails
        val player =
            playerRepository.findByIdForUpdate(playerId)
                ?: throw IllegalArgumentException(
                    "Player not found"
                )

        val bonus =
            bonusRepository.findFirstByPlayerIdAndStatus(
                playerId = playerId,
                status = BonusStatus.ACTIVE
            ) ?: return

        val now = Instant.now()

        if (now.isBefore(bonus.expiresAt)) {
            return
        }

        val amountToForfeit =
            player.bonusBalance

        if (amountToForfeit > BigDecimal.ZERO) {

            player.bonusBalance =
                BigDecimal("0.00")

            ledgerService.record(
                player = player,
                type = LedgerEntryType.BONUS_FORFEITED,
                realChange = BigDecimal("0.00"),
                bonusChange = amountToForfeit.negate(),
                referenceType = "BONUS",
                referenceId = bonus.id,
                description = "Expired bonus forfeited"
            )
        }

        bonus.status = BonusStatus.EXPIRED
        bonus.completedAt = now
    }
}