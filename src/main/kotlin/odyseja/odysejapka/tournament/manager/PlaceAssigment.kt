package odyseja.odysejapka.tournament.manager

internal class PlaceAssigment(private val computedScores: List<ComputedTeamScores>) {

    private var currentPlace = 0
    private var lastPlaceScore = Double.NaN

    fun assignPlaces(): List<FinalTeamScore> {
        return computedScores.sortedByDescending { it.total }.map {
            setCurrentPlace(it)
            FinalTeamScore(
                place = currentPlace,
                longTermScore = it.scaledLongTerm,
                spontaneousScore = it.scaledSpontaneous,
                styleScore = it.scaledStyle,
                balsaScore = it.scaledBalsa,
                penalty = it.penalty,
                teamName = it.team.teamName,
                total = it.total
            )
        }
    }

    private fun setCurrentPlace(teamScore: ComputedTeamScores) {
        if (isDraw(teamScore)) {
            currentPlace++
            lastPlaceScore = teamScore.total
            return
        }

        if (lastPlaceScore.isNaN()) {
            lastPlaceScore = teamScore.total
        }
    }

    private fun isDraw(teamScore: ComputedTeamScores): Boolean {
        val diff = getLastPlaceScore() - teamScore.total
        return diff >= 1.0
    }

    private fun getLastPlaceScore(): Double {
        return if (lastPlaceScore.isNaN()) Double.POSITIVE_INFINITY else lastPlaceScore
    }
}