package com.casino.wallet.bonus

import com.casino.wallet.deposit.Deposit
import com.casino.wallet.player.Player
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "bonuses")
class Bonus(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    val player: Player,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deposit_id", nullable = false, unique = true)
    val deposit: Deposit,

    @Column(name = "initial_amount", nullable = false, precision = 19, scale = 2)
    val initialAmount: BigDecimal,

    @Column(name = "wagering_required", nullable = false, precision = 19, scale = 2)
    val wageringRequired: BigDecimal,

    @Column(name = "wagering_completed", nullable = false, precision = 19, scale = 2)
    var wageringCompleted: BigDecimal = BigDecimal.ZERO.setScale(2),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BonusStatus = BonusStatus.ACTIVE,

    @Column(name = "granted_at", nullable = false)
    val grantedAt: Instant = Instant.now(),

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,

    @Column(name = "completed_at")
    var completedAt: Instant? = null
)