package odyseja.odysejapka.rak

import odyseja.odysejapka.gad.Team
import odyseja.odysejapka.gad.TeamsGroup
import odyseja.odysejapka.gad.TeamsGroups

class RakCalculator {

    fun calculateScores(teams: List<Team>, isRegion: Boolean = false): List<FinalScoreGroup> {
        if (teams.isEmpty()) return emptyList()
        val tournamentGroups = TeamsGroups.fromTeams(teams).groups
        return tournamentGroups.map { scoreGroup(it, isRegion) }
    }

    private fun scoreGroup(
        teamsGroup: TeamsGroup,
        isRegion: Boolean = false
    ): FinalScoreGroup {
        val (group, groupTeams) = teamsGroup
        val maxLT = groupTeams.maxOfOrNull { it.longTermScore ?: 0f } ?: 0f
        val maxSP = groupTeams.maxOfOrNull { it.spontaneousScore ?: 0f } ?: 0f
        val maxST = groupTeams.maxOfOrNull { it.styleScore ?: 0f } ?: 0f
        val maxWeightHeld = groupTeams.maxOfOrNull { it.weightHeld ?: 0f } ?: 0f

        val scaledScores = groupTeams.map { team ->
            TeamScorer(team).score(maxLT, maxST, maxSP, maxWeightHeld)
        }

        return FinalScoreGroup(
            problem = group.problem,
            division = group.age,
            league = group.league,
            teamScores = PlaceAssigment(scaledScores).assignPlaces(isRegion)
        )
    }
}

data class FinalScoreGroup(
    val problem: Int,
    val division: Int,
    val league: String,
    val teamScores: List<FinalTeamScore>
)

data class FinalTeamScore(
    val team: Team,
    val place: Int,
    val isWinner: Boolean,
    val longTermScore: Double,
    val balsaScore: Double,
    val spontaneousScore: Double,
    val styleScore: Double,
    val penalty: Double,
    val teamName: String,
    val total: Double,
)