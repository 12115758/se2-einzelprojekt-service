package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin
import kotlin.test.assertFailsWith

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectTimeSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0]) //braucht 10 Sek.
        assertEquals(third, res[1]) //braucht 15 Sek.
        assertEquals(first, res[2]) //braucht 20 Sek.
    }

    @Test
    fun test_getLeaderboard_withRank_returnsCorrectSublist() {
        val player1 = GameResult(1, "player1", 100, 10.0)
        val player2 = GameResult(2, "player2", 90, 10.0)
        val player3 = GameResult(3, "player3", 80, 10.0)
        val player4 = GameResult(4, "player4", 70, 10.0)
        val player5 = GameResult(5, "player5", 60, 10.0)
        val player6 = GameResult(6, "player6", 50, 10.0)
        val player7 = GameResult(7, "player7", 40, 10.0)
        val player8 = GameResult(8, "player8", 30, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(
            listOf(player8, player3, player6, player1, player4, player2, player7, player5)
        )

        val res: List<GameResult> = controller.getLeaderboard(4)

        verify(mockedService).getGameResults()
        assertEquals(7, res.size)

        assertEquals(player1, res[0])
        assertEquals(player2, res[1])
        assertEquals(player3, res[2])
        assertEquals(player4, res[3])
        assertEquals(player5, res[4])
        assertEquals(player6, res[5])
        assertEquals(player7, res[6])
    }

    @Test
    fun test_getLeaderboard_withInvalidRank_throwsException() {
        val player1 = GameResult(1, "player1", 100, 10.0)
        val player2 = GameResult(2, "player2", 90, 10.0)
        val player3 = GameResult(3, "player3", 80, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(
            listOf(player1, player2, player3)
        )

        assertFailsWith<org.springframework.web.server.ResponseStatusException> {
            controller.getLeaderboard(0)
        }

        verify(mockedService).getGameResults()
    }

    @Test
    fun test_getLeaderboard_withTooLargeRank_throwsException() {
        val player1 = GameResult(1, "player1", 100, 10.0)
        val player2 = GameResult(2, "player2", 90, 10.0)
        val player3 = GameResult(3, "player3", 80, 10.0)

        whenever(mockedService.getGameResults()).thenReturn(
            listOf(player1, player2, player3)
        )

        assertFailsWith<org.springframework.web.server.ResponseStatusException> {
            controller.getLeaderboard(10)
        }

        verify(mockedService).getGameResults()
    }

}