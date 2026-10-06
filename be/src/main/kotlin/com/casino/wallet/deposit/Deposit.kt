package com.casino.wallet.deposit

import com.casino.wallet.player.Player
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "deposits")
class Deposit(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    val player: Player,

    @Column(nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: DepositStatus = DepositStatus.PENDING,

    @Column(name = "provider_reference")
    var providerReference: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "completed_at")
    var completedAt: Instant? = null
)