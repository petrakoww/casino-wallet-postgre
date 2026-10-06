package com.casino.wallet.betting

import com.casino.wallet.player.Player
import com.casino.wallet.player.PlayerRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
@Testcontainers
class BetConcurrencyIntegrationTest {

    @Autowired
    lateinit var bettingService: BettingService

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    companion object {
        // testcontainers starts new db instead of using local casino_wallet db
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres =
            PostgreSQLContainer("postgres:17-alpine")
    }

    @BeforeEach
    fun cleanDatabase() {
        // clear tables before every test
        jdbcTemplate.execute(
            """
            TRUNCATE TABLE
                ledger_entries,
                bets,
                bonuses,
                deposits,
                players
            CASCADE
            """
        )
    }

    @Test
    fun `two simultaneous eight euro bets against ten euro balance allow exactly one`() {
        // player has 10 money and no bonus
        val player = playerRepository.saveAndFlush(
            Player(
                username = "concurrency-player",
                realBalance = BigDecimal("10.00"),
                bonusBalance = BigDecimal("0.00")
            )
        )

        // two worker threads simulate two HTTP requests arriving almost simultaneously
        val executor = Executors.newFixedThreadPool(2)

        // wait for both of them to be ready
        val readyLatch = CountDownLatch(2)

        val startLatch = CountDownLatch(1)

        val request =
            PlaceBetRequest(
                stake = BigDecimal("8.00")
            )

        val futures = (1..2).map {

            executor.submit<Result<BetResponse>> {
                readyLatch.countDown()
                startLatch.await()

                runCatching {
                    bettingService.placeBet(
                        playerId = player.id,
                        request = request
                    )
                }
            }
        }

        assertTrue(
            readyLatch.await(
                5,
                TimeUnit.SECONDS
            )
        )

        startLatch.countDown()

        val results =
            futures.map { future ->
                future.get(
                    10,
                    TimeUnit.SECONDS
                )
            }

        executor.shutdown()

        val successes =
            results.count { it.isSuccess }

        val failures =
            results.count { it.isFailure }

        assertEquals(
            1,
            successes,
            "Exactly one bet must succeed"
        )

        assertEquals(
            1,
            failures,
            "Exactly one bet must fail"
        )

        // balance should be 2
        val playerAfterBets =
            playerRepository.findById(player.id)
                .orElseThrow()

        assertEquals(
            0,
            BigDecimal("2.00")
                .compareTo(playerAfterBets.realBalance)
        )

        assertEquals(
            0,
            BigDecimal("0.00")
                .compareTo(playerAfterBets.bonusBalance)
        )

        // only one bet should be created
        val betCount =
            jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM bets
                WHERE player_id = ?
                """,
                Long::class.java,
                player.id
            )

        assertEquals(
            1L,
            betCount
        )

        val ledgerCount =
            jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM ledger_entries
                WHERE player_id = ?
                  AND type = 'BET_STAKE'
                """,
                Long::class.java,
                player.id
            )

        assertEquals(
            1L,
            ledgerCount
        )
    }
}