package com.casino.wallet.betting

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
class BetSettlementService(
    private val betRepository: BetRepository,
    private val playerRepository: PlayerRepository,
    private val ledgerService: LedgerService
) {

    @Transactional
    fun settle(
        playerId: UUID,
        betId: UUID,
        request: SettleBetRequest
    ): SettledBetResponse {

        // lock bet to prevent concurrent settlement
        val bet =
            betRepository.findByIdForUpdate(betId)
                ?: throw IllegalArgumentException(
                    "Bet not found"
                )

        // check if the bet belongs to the player
        if (bet.player.id != playerId) {
            throw IllegalArgumentException(
                "Bet does not belong to player"
            )
        }

        // bet can be settled only once
        if (bet.status == BetStatus.SETTLED) {
            throw IllegalArgumentException(
                "Bet already settled"
            )
        }

        // lock the player's wallet to prevent race conditions with deposits or other bets for balances
        val player =
            playerRepository.findByIdForUpdate(playerId)
                ?: throw IllegalArgumentException(
                    "Player not found"
                )

        val winAmount =
            request.winAmount.setScale(
                2,
                RoundingMode.UNNECESSARY
            )

        // lost bet
        //
        // stake has already been deducted in placeBet() - when win = 0 - no balance movement.
        if (winAmount.compareTo(BigDecimal.ZERO) == 0) {

            bet.winAmount = BigDecimal("0.00")
            bet.realWin = BigDecimal("0.00")
            bet.bonusWin = BigDecimal("0.00")
            bet.status = BetStatus.SETTLED
            bet.settledAt = Instant.now()

            return SettledBetResponse.from(bet)
        }

        // real stake ratio
        val realRatio =
            bet.realStake.divide(
                bet.stake,
                10,
                RoundingMode.HALF_UP
            )

        // real part of the win (ratio)
        val realWin =
            winAmount.multiply(realRatio)
                .setScale(
                    2,
                    RoundingMode.HALF_UP
                )

        val bonusWin =
            winAmount.subtract(realWin)

        bet.winAmount = winAmount
        bet.realWin = realWin
        bet.bonusWin = bonusWin
        bet.status = BetStatus.SETTLED
        bet.settledAt = Instant.now()

        // if the bonus used for this bet is still ACTIVE, the bonus part of the win remains bonus money
        val sourceBonusStillActive =
            bet.bonus?.status == BonusStatus.ACTIVE

        if (sourceBonusStillActive) {

            player.realBalance =
                player.realBalance.add(realWin)

            player.bonusBalance =
                player.bonusBalance.add(bonusWin)

            ledgerService.record(
                player = player,
                type = LedgerEntryType.BET_WIN,
                realChange = realWin,
                bonusChange = bonusWin,
                referenceType = "BET",
                referenceId = bet.id,
                description = "Bet settled with win"
            )

        } else {

            // if the wagering has already ended or the bonus is no longer active, there is no point in creating a locked bonus balance again
            // we still keep the original split in bet.realWin and bet.bonusWin and credit the entire win as real money
            player.realBalance =
                player.realBalance.add(winAmount)

            ledgerService.record(
                player = player,
                type = LedgerEntryType.BET_WIN,
                realChange = winAmount,
                bonusChange = BigDecimal("0.00"),
                referenceType = "BET",
                referenceId = bet.id,
                description = "Bet settled after bonus ended"
            )
        }

        return SettledBetResponse.from(bet)
    }
}