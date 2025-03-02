package odyseja.odysejapka.rak

import Team

internal class TeamScorer(private val team: Team) {

    fun score(maxLT: Float, maxST: Float, maxSP: Float, maxWeightHeld: Float): ComputedTeamScores {
        val scaledSP = scale(team.spontaneousScore ?: 0f, maxSP, 100.0)
        val scaledST = scale(team.styleScore ?: 0f, maxST, 50.0)
        val scaledBalsa = scale(team.weightHeld ?: 0f, maxWeightHeld, 100.0)
        val scaledLT = calculateLt(team, maxLT, scaledBalsa)

        val total = scaledLT + scaledSP + scaledST - (team.penaltyScore ?: 0f)

        return ComputedTeamScores(
            team = team,
            scaledLongTerm = scaledLT,
            scaledSpontaneous = scaledSP,
            scaledStyle = scaledST,
            scaledBalsa = scaledBalsa,
            penalty = (team.penaltyScore ?: 0f).toDouble(),
            total = total
        )
    }

    private fun calculateLt(team: Team, maxLT: Float, scaledBalsa: Double): Double {
        if (team.isBalsa()) {
            return scale(team.longTermScore ?: 0f, maxLT, 100.0) + scaledBalsa
        }
        return scale(team.longTermScore ?: 0f, maxLT, 200.0)
    }

    private fun scale(raw: Float, maxRaw: Float, maxScaled: Double): Double {
        if (maxRaw <= 0f) return 0.0
        return (raw / maxRaw) * maxScaled
    }
}