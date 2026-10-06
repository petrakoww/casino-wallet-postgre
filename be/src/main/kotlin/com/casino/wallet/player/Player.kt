package com.casino.wallet.player

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "players")
class Player(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(
        name = "real_balance",
        nullable = false,
        precision = 19,
        scale = 2
    )
    var realBalance: BigDecimal = BigDecimal.ZERO.setScale(2),

    @Column(
        name = "bonus_balance",
        nullable = false,
        precision = 19,
        scale = 2
    )
    var bonusBalance: BigDecimal = BigDecimal.ZERO.setScale(2),

    @Version
    var version: Long = 0,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)