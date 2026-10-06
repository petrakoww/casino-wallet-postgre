package com.casino.wallet.bonus

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BonusProgressResponse(
    val active: Boolean,
    val bonusId: UUID?,
    val initialAmount: BigDecimal,
    val wageringRequired: BigDecimal,
    val wageringCompleted: BigDecimal,
    val wageringRemaining: BigDecimal,
    val progressPercent: BigDecimal,
    val expiresAt: Instant?,
    val status: BonusStatus?
)