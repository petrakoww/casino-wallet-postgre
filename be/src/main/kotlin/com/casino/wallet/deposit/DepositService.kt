package com.casino.wallet.deposit

import com.casino.wallet.player.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.RoundingMode
import java.util.UUID

@Service
class DepositService(
    private val playerRepository: PlayerRepository,
    private val depositRepository: DepositRepository
) {

    @Transactional
    fun createDeposit(
        playerId: UUID,
        request: CreateDepositRequest
    ): DepositResponse {

        val player = playerRepository.findById(playerId)
            .orElseThrow {
                IllegalArgumentException("Player not found")
            }

        val amount = request.amount
            .setScale(2, RoundingMode.UNNECESSARY)

        val deposit = Deposit(
            player = player,
            amount = amount
        )

        val saved = depositRepository.save(deposit)

        return DepositResponse.from(saved)
    }
}