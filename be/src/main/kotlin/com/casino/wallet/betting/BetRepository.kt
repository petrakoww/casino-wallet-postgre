package com.casino.wallet.betting

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface BetRepository : JpaRepository<Bet, UUID> {

    // lock the bet to prevent concurrent settlement
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        select b
        from Bet b
        where b.id = :id
        """
    )
    fun findByIdForUpdate(
        @Param("id") id: UUID
    ): Bet?
}