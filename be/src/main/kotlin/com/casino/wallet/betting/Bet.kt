package com.casino.wallet.betting

import com.casino.wallet.player.Player
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import com.casino.wallet.bonus.Bonus

@Entity
@Table(name = "bets")
class Bet(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    val player: Player,


    // store the bonus that was active when the bet was placed to know where the bonus stake came from
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bonus_id")
    val bonus: Bonus? = null,

    @Column(nullable = false, precision = 19, scale = 2)
    val stake: BigDecimal,

    @Column(name = "real_stake", nullable = false, precision = 19, scale = 2)
    val realStake: BigDecimal,

    @Column(name = "bonus_stake", nullable = false, precision = 19, scale = 2)
    val bonusStake: BigDecimal,

    @Column(name = "win_amount", precision = 19, scale = 2)
    var winAmount: BigDecimal? = null,

    @Column(name = "real_win", precision = 19, scale = 2)
    var realWin: BigDecimal? = null,

    @Column(name = "bonus_win", precision = 19, scale = 2)
    var bonusWin: BigDecimal? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BetStatus = BetStatus.OPEN,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "settled_at")
    var settledAt: Instant? = null
)