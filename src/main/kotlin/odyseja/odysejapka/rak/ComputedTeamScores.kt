package odyseja.odysejapka.rak

import odyseja.odysejapka.gad.Team

data class ComputedTeamScores(
    val team: Team,
    val scaledLongTerm: Double,
    val scaledSpontaneous: Double,
    val scaledStyle: Double,
    val scaledBalsa: Double,
    val penalty: Double,
    val total: Double
)