package odyseja.odysejapka.tournament.manager

import Team
import kotlin.math.abs

class TMCalculator {

    fun calculateScores(teams: List<Team>): List<FinalScoreGroup> {
        if (teams.isEmpty()) return emptyList()

        val grouped: Map<Triple<Int, Int, Int>, List<Team>> = teams.groupBy {
            Triple(it.problem(), it.division(), it.leagueInt())
        }

        val results = mutableListOf<FinalScoreGroup>()

        for ((groupKey, groupTeams) in grouped) {
            val (problem, division, league) = groupKey

            val maxLT = groupTeams.maxOfOrNull { it.longTermScore ?: 0f } ?: 0f
            val maxSP = groupTeams.maxOfOrNull { it.spontaneousScore ?: 0f } ?: 0f
            val maxST = groupTeams.maxOfOrNull { it.styleScore ?: 0f } ?: 0f

            val computedScores = groupTeams.map { team ->
                val scaledLT = scale(team.longTermScore ?: 0f, maxLT, 200.0)
                val scaledSP = scale(team.spontaneousScore ?: 0f, maxSP, 100.0)
                val scaledST = scale(team.styleScore ?: 0f, maxST, 50.0)

                val balsa = 0.0

                val total = scaledLT + scaledSP + scaledST - (team.penaltyScore ?: 0f)

                ComputedTeamScores(
                    team = team,
                    scaledLongTerm = scaledLT,
                    scaledSpontaneous = scaledSP,
                    scaledStyle = scaledST,
                    scaledBalsa = balsa,
                    penalty = (team.penaltyScore ?: 0f).toDouble(),
                    total = total
                )
            }

            val sorted = computedScores.sortedByDescending { it.total }

            var currentPlace = 1
            var placeScore = Double.NaN
            var teamsAtThisPlace = 0

            val finalTeamScores = mutableListOf<FinalTeamScore>()

            for (i in sorted.indices) {
                val teamScore = sorted[i]
                val diff = if (placeScore.isNaN()) Double.POSITIVE_INFINITY else placeScore - teamScore.total

                if (diff >= 1.0) {
                    currentPlace += teamsAtThisPlace
                    placeScore = teamScore.total
                    teamsAtThisPlace = 1
                } else {
                    if (placeScore.isNaN()) {
                        placeScore = teamScore.total
                    }
                    teamsAtThisPlace++
                }

                finalTeamScores += FinalTeamScore(
                    place = currentPlace,
                    longTermScore = teamScore.scaledLongTerm,
                    spontaneousScore = teamScore.scaledSpontaneous,
                    styleScore = teamScore.scaledStyle,
                    balsaScore = teamScore.scaledBalsa,
                    penalty = teamScore.penalty,
                    teamName = teamScore.team.teamName
                )
            }

            val group = FinalScoreGroup(
                problem = problem,
                division = division,
                league = league,
                teamScores = finalTeamScores
            )
            results += group
        }

        return results
    }

    private fun scale(raw: Float, maxRaw: Float, maxScaled: Double): Double {
        if (maxRaw <= 0f) return 0.0
        return (raw / maxRaw) * maxScaled
    }

    private fun Team.problem(): Int {
        return code.substringAfter("P").substringBefore("G").toIntOrNull() ?: 0
    }

    private fun Team.division(): Int {
        return code.substringAfter("G").toIntOrNull() ?: 0
    }

    private fun Team.leagueInt(): Int {
        return league.toIntOrNull() ?: 0
    }
}

private data class ComputedTeamScores(
    val team: Team,
    val scaledLongTerm: Double,
    val scaledSpontaneous: Double,
    val scaledStyle: Double,
    val scaledBalsa: Double,
    val penalty: Double,
    val total: Double
)

data class FinalScoreGroup(val problem: Int, val division: Int, val league: Int, val teamScores: List<FinalTeamScore>)

data class FinalTeamScore(
    val place: Int,
    val longTermScore: Double,
    val balsaScore: Double,
    val spontaneousScore: Double,
    val styleScore: Double,
    val penalty: Double,
    val teamName: String
)