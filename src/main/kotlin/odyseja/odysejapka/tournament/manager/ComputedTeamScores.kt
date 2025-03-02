package odyseja.odysejapka.tournament.manager

import Team

data class ComputedTeamScores(
    val team: Team,
    val scaledLongTerm: Double,
    val scaledSpontaneous: Double,
    val scaledStyle: Double,
    val scaledBalsa: Double,
    val penalty: Double,
    val total: Double
)