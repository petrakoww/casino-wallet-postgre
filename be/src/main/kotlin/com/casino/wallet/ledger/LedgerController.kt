package com.casino.wallet.ledger

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/players/{playerId}/ledger")
class LedgerController(
    private val ledgerService: LedgerService
) {

    @GetMapping
    fun getLedger(
        @PathVariable playerId: UUID,

        @RequestParam(defaultValue = "0")
        page: Int,

        @RequestParam(defaultValue = "10")
        size: Int
    ): LedgerPageResponse {

        return ledgerService.getPage(
            playerId = playerId,
            page = page,
            size = size
        )
    }
}