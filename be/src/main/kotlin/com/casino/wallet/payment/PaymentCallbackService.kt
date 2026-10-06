package com.casino.wallet.payment

import com.casino.wallet.bonus.BonusService
import com.casino.wallet.deposit.DepositRepository
import com.casino.wallet.deposit.DepositStatus
import com.casino.wallet.ledger.LedgerEntryType
import com.casino.wallet.ledger.LedgerService
import com.casino.wallet.player.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

@Service
class PaymentCallbackService(
    private val depositRepository: DepositRepository,
    private val playerRepository: PlayerRepository,
    private val ledgerService: LedgerService,
    private val bonusService: BonusService
) {

    @Transactional
    fun process(
        request: PaymentCallbackRequest
    ): PaymentCallbackResponse {
        // lock deposit
        // in case of two callback, only one will be accepted
        val deposit = depositRepository.findByIdForUpdate(
            request.depositId
        ) ?: throw IllegalArgumentException("Deposit not found")

        // amount in cents to prevent floating point issues
        val callbackAmount = BigDecimal.valueOf(
            request.amountCents,
            2
        ).setScale(
            2,
            RoundingMode.UNNECESSARY
        )

        // provider should not be able to confirm a deposit with a different amount than the one we created
        if (callbackAmount.compareTo(deposit.amount) != 0) {
            throw IllegalArgumentException(
                "Callback amount does not match deposit amount"
            )
        }

        // Idempotency
        // if deposit is successful, do not credit the player again

        if (deposit.status == DepositStatus.COMPLETED) {

            if (deposit.providerReference != request.providerReference) {
                throw IllegalArgumentException(
                    "Deposit already completed with another provider reference"
                )
            }

            return PaymentCallbackResponse(
                depositId = deposit.id,
                credited = false,
                amount = deposit.amount
            )
        }

        // lock player
        // update it synchronously
        val player = playerRepository.findByIdForUpdate(
            deposit.player.id
        ) ?: throw IllegalArgumentException("Player not found")

        // count the completed deposits to check if this one is the first for welcome bonus
        val completedDepositsBeforeThisOne =
            depositRepository.countByPlayerIdAndStatus(
                playerId = player.id,
                status = DepositStatus.COMPLETED
            )

        // unique provider reference
        val existing = depositRepository.findByProviderReference(
            request.providerReference
        )

        if (existing != null && existing.id != deposit.id) {
            throw IllegalArgumentException(
                "Provider reference already processed"
            )
        }

        player.realBalance =
            player.realBalance.add(deposit.amount)

        deposit.status = DepositStatus.COMPLETED
        deposit.providerReference = request.providerReference
        deposit.completedAt = Instant.now()

        playerRepository.save(player)
        depositRepository.save(deposit)

        ledgerService.record(
            player = player,
            type = LedgerEntryType.DEPOSIT,
            realChange = deposit.amount,
            bonusChange = BigDecimal("0.00"),
            referenceType = "DEPOSIT",
            referenceId = deposit.id,
            description = "Deposit completed"
        )

        // welcome bonus check: isFirst, min 20, cap 100, etc.
        bonusService.grantWelcomeBonusIfEligible(
            player = player,
            deposit = deposit,
            completedDepositsBeforeThisOne =
                completedDepositsBeforeThisOne
        )

        return PaymentCallbackResponse(
            depositId = deposit.id,
            credited = true,
            amount = deposit.amount
        )
    }
}