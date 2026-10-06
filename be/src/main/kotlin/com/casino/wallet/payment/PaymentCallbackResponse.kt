package com.casino.wallet.payment

import java.math.BigDecimal
import java.util.UUID

data class PaymentCallbackResponse(
    val depositId: UUID,
    val credited: Boolean,
    val amount: BigDecimal
)