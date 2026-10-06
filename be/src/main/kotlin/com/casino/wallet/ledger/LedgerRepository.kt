package com.casino.wallet.ledger

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LedgerRepository : JpaRepository<LedgerEntry, UUID> {

    fun findAllByPlayerId(
        playerId: UUID,
        pageable: Pageable
    ): Page<LedgerEntry>
}