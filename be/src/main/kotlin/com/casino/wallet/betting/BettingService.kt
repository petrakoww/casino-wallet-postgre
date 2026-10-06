package com.casino.wallet.betting

import com.casino.wallet.bonus.BonusExpirationService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BettingService(
    private val bonusExpirationService: BonusExpirationService,
    private val betTransactionService: BetTransactionService
) {

    fun placeBet(
        playerId: UUID,
        request: PlaceBetRequest
    ): BetResponse {

        // handle expired bonus
        bonusExpirationService.expireIfNeeded(
            playerId
        )

        // bet in a separate transaction
        return betTransactionService.placeBet(
            playerId = playerId,
            request = request
        )
    }
}