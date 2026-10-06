package com.casino.wallet.bonus

import com.casino.wallet.deposit.Deposit
import com.casino.wallet.ledger.LedgerEntryType
import com.casino.wallet.ledger.LedgerService
import com.casino.wallet.player.Player
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class BonusService(
    private val bonusRepository: BonusRepository,
    private val ledgerService: LedgerService
) {

    companion object {
        private val MINIMUM_DEPOSIT =
            BigDecimal("20.00")

        private val MAXIMUM_BONUS =
            BigDecimal("100.00")

        private val WAGERING_MULTIPLIER =
            BigDecimal("20")
    }

    fun grantWelcomeBonusIfEligible(
        player: Player,
        deposit: Deposit,
        completedDepositsBeforeThisOne: Long
    ): Bonus? {

        // welcome bonus on first SUCCESSFUL deposit only
        if (completedDepositsBeforeThisOne > 0) {
            return null
        }

        // wb is at least 20
        if (deposit.amount < MINIMUM_DEPOSIT) {
            return null
        }

        // wb is max 100
        val bonusAmount =
            deposit.amount.min(MAXIMUM_BONUS)

        // wagering *20 in this case
        val wageringRequired =
            bonusAmount.multiply(WAGERING_MULTIPLIER)

        val now = Instant.now()

        val bonus = Bonus(
            player = player,
            deposit = deposit,
            initialAmount = bonusAmount,
            wageringRequired = wageringRequired,
            wageringCompleted = BigDecimal("0.00"),
            status = BonusStatus.ACTIVE,
            grantedAt = now,
            expiresAt = now.plus(7, ChronoUnit.DAYS)
        )

        player.bonusBalance =
            player.bonusBalance.add(bonusAmount)

        val savedBonus =
            bonusRepository.save(bonus)

        ledgerService.record(
            player = player,
            type = LedgerEntryType.BONUS_GRANTED,
            realChange = BigDecimal("0.00"),
            bonusChange = bonusAmount,
            referenceType = "BONUS",
            referenceId = savedBonus.id,
            description = "Welcome bonus granted"
        )

        return savedBonus
    }


    // check if the bonus has expired and forfeit the remaining bonus balance if it has
    // returns ACTIVE or null if there is no active bonus
    fun expireIfNeeded(
        player: Player,
        bonus: Bonus?
    ): Bonus? {

        if (bonus == null) {
            return null
        }

        val now = Instant.now()

        if (now.isBefore(bonus.expiresAt)) {
            return bonus
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

        bonusRepository.save(bonus)

        return null
    }


    // move the bonus balance to real balance if wagering requirement is completed
    fun recordWagering(
        player: Player,
        bonus: Bonus?,
        stake: BigDecimal
    ) {

        if (bonus == null) {
            return
        }

        bonus.wageringCompleted =
            bonus.wageringCompleted.add(stake)

        if (
            bonus.wageringCompleted <
            bonus.wageringRequired
        ) {
            bonusRepository.save(bonus)
            return
        }

        // wagering is completed, convert bonus balance to real balance
        val amountToConvert =
            player.bonusBalance

        if (amountToConvert > BigDecimal.ZERO) {

            player.bonusBalance =
                player.bonusBalance.subtract(amountToConvert)

            player.realBalance =
                player.realBalance.add(amountToConvert)

            ledgerService.record(
                player = player,
                type = LedgerEntryType.BONUS_CONVERTED,
                realChange = amountToConvert,
                bonusChange = amountToConvert.negate(),
                referenceType = "BONUS",
                referenceId = bonus.id,
                description = "Wagering completed; bonus converted to real money"
            )
        }

        bonus.status = BonusStatus.COMPLETED
        bonus.completedAt = Instant.now()

        bonusRepository.save(bonus)
    }

    fun getProgress(
        playerId: UUID
    ): BonusProgressResponse {

        val bonus =
            bonusRepository.findFirstByPlayerIdAndStatus(
                playerId = playerId,
                status = BonusStatus.ACTIVE
            )

        if (bonus == null) {
            return BonusProgressResponse(
                active = false,
                bonusId = null,
                initialAmount = BigDecimal("0.00"),
                wageringRequired = BigDecimal("0.00"),
                wageringCompleted = BigDecimal("0.00"),
                wageringRemaining = BigDecimal("0.00"),
                progressPercent = BigDecimal("0.00"),
                expiresAt = null,
                status = null
            )
        }

        // at least 0
        val remaining =
            bonus.wageringRequired
                .subtract(bonus.wageringCompleted)
                .max(BigDecimal.ZERO)
                .setScale(2)

        val progressPercent =
            if (bonus.wageringRequired.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal("100.00")
            } else {
                bonus.wageringCompleted
                    .multiply(BigDecimal("100"))
                    .divide(
                        bonus.wageringRequired,
                        2,
                        java.math.RoundingMode.HALF_UP
                    )
                    .min(BigDecimal("100.00"))
            }

        return BonusProgressResponse(
            active = true,
            bonusId = bonus.id,
            initialAmount = bonus.initialAmount,
            wageringRequired = bonus.wageringRequired,
            wageringCompleted = bonus.wageringCompleted,
            wageringRemaining = remaining,
            progressPercent = progressPercent,
            expiresAt = bonus.expiresAt,
            status = bonus.status
        )
    }
}