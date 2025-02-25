package odyseja.odysejapka.tournament.manager

import Team

class TMCalculator() {

    fun calculateScores(teams: List<Team>): List<FinalScoreGroup> {
        return listOf()
    }
}

data class FinalScoreGroup(val problem: Int, val division: Int, val league: Int, val teamScores: List<FinalTeamScore>)

data class FinalTeamScore(
    val place: Int,
    val longTermScore: Double,
    val balsaScore: Double,
    val styleScore: Double,
    val penalty: Double,
    val teamName: String
)