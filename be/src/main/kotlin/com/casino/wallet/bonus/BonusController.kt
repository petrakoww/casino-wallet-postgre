package com.casino.wallet.bonus

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/players/{playerId}/bonus")
class BonusController(
    private val bonusService: BonusService
) {

    @GetMapping
    fun getProgress(
        @PathVariable playerId: UUID
    ): BonusProgressResponse {

        return bonusService.getProgress(playerId)
    }
}