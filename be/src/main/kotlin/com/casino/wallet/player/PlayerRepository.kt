package com.casino.wallet.player

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface PlayerRepository : JpaRepository<Player, UUID> {

    fun findByUsername(
        username: String
    ): Player?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        select p
        from Player p
        where p.id = :id
        """
    )
    fun findByIdForUpdate(
        @Param("id") id: UUID
    ): Player?
}