package com.casino.wallet.player

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerService(
    private val playerRepository: PlayerRepository
) {

    @Transactional
    fun create(
        request: CreatePlayerRequest
    ): PlayerResponse {

        val player = Player(
            username = request.username
        )

        return PlayerResponse.from(
            playerRepository.save(player)
        )
    }

    fun getWallet(
        playerId: java.util.UUID
    ): WalletResponse {

        val player =
            playerRepository.findById(playerId)
                .orElseThrow {
                    IllegalArgumentException("Player not found")
                }

        return WalletResponse.from(player)
    }

    @Transactional
    fun loginOrCreate(
        request: LoginPlayerRequest
    ): PlayerResponse {

        val username =
            request.username.trim()

        // if player exists return him
        val existing =
            playerRepository.findByUsername(
                username
            )

        if (existing != null) {
            return PlayerResponse.from(existing)
        }

        // else create new
        val player =
            Player(
                username = username
            )

        return PlayerResponse.from(
            playerRepository.save(player)
        )
    }
}