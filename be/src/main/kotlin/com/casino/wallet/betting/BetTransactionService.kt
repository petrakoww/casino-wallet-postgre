package com.casino.wallet.betting

import com.casino.wallet.bonus.BonusRepository
import com.casino.wallet.bonus.BonusService
import com.casino.wallet.bonus.BonusStatus
import com.casino.wallet.ledger.LedgerEntryType
import com.casino.wallet.ledger.LedgerService
import com.casino.wallet.player.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.UUID

@Service
class BetTransactionService(
    private val playerRepository: PlayerRepository,
    private val bonusRepository: BonusRepository,
    private val bonusService: BonusService,
    private val betRepository: BetRepository,
    private val ledgerService: LedgerService
) {

    companion object {
        private val MAX_STAKE_WITH_ACTIVE_BONUS =
            BigDecimal("5.00")
    }

    @Transactional
    fun placeBet(
        playerId: UUID,
        request: PlaceBetRequest
    ): BetResponse {

        val player =
            playerRepository.findByIdForUpdate(playerId)
                ?: throw IllegalArgumentException(
                    "Player not found"
                )

        val stake =
            request.stake.setScale(
                2,
                RoundingMode.UNNECESSARY
            )

        val activeBonus =
            bonusRepository.findFirstByPlayerIdAndStatus(
                playerId = player.id,
                status = BonusStatus.ACTIVE
            )

        if (
            activeBonus != null &&
            stake > MAX_STAKE_WITH_ACTIVE_BONUS
        ) {
            throw IllegalArgumentException(
                "Maximum stake while a bonus is active is 5.00 EUR"
            )
        }

        val totalBalance =
            player.realBalance.add(
                player.bonusBalance
            )

        if (stake > totalBalance) {
            throw IllegalArgumentException(
                "Insufficient balance"
            )
        }

        val realStake =
            stake.min(player.realBalance)

        val bonusStake =
            stake.subtract(realStake)

        player.realBalance =
            player.realBalance.subtract(
                realStake
            )

        player.bonusBalance =
            player.bonusBalance.subtract(
                bonusStake
            )

        val bet =
            betRepository.save(
                Bet(
                    player = player,
                    bonus = activeBonus,
                    stake = stake,
                    realStake = realStake,
                    bonusStake = bonusStake,
                    status = BetStatus.OPEN,
                    createdAt = Instant.now()
                )
            )

        ledgerService.record(
            player = player,
            type = LedgerEntryType.BET_STAKE,
            realChange = realStake.negate(),
            bonusChange = bonusStake.negate(),
            referenceType = "BET",
            referenceId = bet.id,
            description = "Bet stake placed"
        )

        bonusService.recordWagering(
            player = player,
            bonus = activeBonus,
            stake = stake
        )

        return BetResponse.from(bet)
    }
}