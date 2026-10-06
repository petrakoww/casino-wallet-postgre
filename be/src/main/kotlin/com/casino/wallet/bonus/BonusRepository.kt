package com.casino.wallet.bonus

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BonusRepository : JpaRepository<Bonus, UUID> {

    fun findFirstByPlayerIdAndStatus(
        playerId: UUID,
        status: BonusStatus
    ): Bonus?
}