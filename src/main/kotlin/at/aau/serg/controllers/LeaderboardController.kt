package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {

        val results = gameResultService.getGameResults()

        val sortedResults = results.sortedWith(
            compareByDescending<GameResult> { it.score } //sortiert zuerst absteigens nach score
                .thenBy { it.timeInSeconds } //bei Gleichstand sortiert nach Zeit
        )

        if (rank == null) { //kein rank -> gesamte Liste ausgegeben
            return sortedResults
        }

        if (rank < 1 || rank > sortedResults.size) { //ungültiger rank?
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank")
        }

        val start = maxOf(0, rank - 4) //berechnet positive Startposition Ausgabe
        val end = minOf(sortedResults.size, rank + 3) //berechnet Endposition für Ausgabe

        return sortedResults.subList(start, end)
    }
}