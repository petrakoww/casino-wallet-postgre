package com.casino.wallet.ledger

data class LedgerPageResponse(
    val content: List<LedgerEntryResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean
)