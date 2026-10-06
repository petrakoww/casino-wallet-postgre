package com.casino.wallet.deposit

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface DepositRepository : JpaRepository<Deposit, UUID> {

    fun findByProviderReference(
        providerReference: String
    ): Deposit?

    fun existsByPlayerIdAndStatus(
        playerId: UUID,
        status: DepositStatus
    ): Boolean

    fun countByPlayerIdAndStatus(
        playerId: UUID,
        status: DepositStatus
    ): Long

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        select d
        from Deposit d
        where d.id = :id
        """
    )
    fun findByIdForUpdate(
        @Param("id") id: UUID
    ): Deposit?
}