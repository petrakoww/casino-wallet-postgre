package com.casino.wallet.ledger

import com.casino.wallet.player.Player
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "ledger_entries")
class LedgerEntry(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    val player: Player,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: LedgerEntryType,

    @Column(name = "real_change", nullable = false, precision = 19, scale = 2)
    val realChange: BigDecimal,

    @Column(name = "bonus_change", nullable = false, precision = 19, scale = 2)
    val bonusChange: BigDecimal,

    @Column(name = "real_balance_after", nullable = false, precision = 19, scale = 2)
    val realBalanceAfter: BigDecimal,

    @Column(name = "bonus_balance_after", nullable = false, precision = 19, scale = 2)
    val bonusBalanceAfter: BigDecimal,

    @Column(name = "reference_type")
    val referenceType: String? = null,

    @Column(name = "reference_id")
    val referenceId: UUID? = null,

    @Column
    val description: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)