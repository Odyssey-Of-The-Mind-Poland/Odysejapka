package odyseja.odysejapka.rak

internal class PlaceAssigment(private val computedScores: List<ComputedTeamScores>) {

    private var currentPlace = 0
    private var lastPlaceScore = Double.NaN

    fun assignPlaces(isRegion: Boolean = false): List<FinalTeamScore> {
        val list = computedScores.sortedByDescending { it.total }.map {
            setCurrentPlace(it)
            FinalTeamScore(
                place = currentPlace,
                isWinner = if (isRegion) false else (currentPlace <= 2 || it.team.ranatra),
                longTermScore = it.scaledLongTerm,
                spontaneousScore = it.scaledSpontaneous,
                styleScore = it.scaledStyle,
                balsaScore = it.scaledBalsa,
                penalty = it.penalty,
                teamName = it.team.teamName,
                total = it.total,
                team = it.team
            )
        }
        return if (isRegion) applyRegionWinnerRules(list) else list
    }

    /**
     * Region (FR) highlighting: 1→1, 2→2, 3→2, 4+→ all except last 2; if tie on second-to-last place then only last one drops.
     */
    private fun applyRegionWinnerRules(list: List<FinalTeamScore>): List<FinalTeamScore> {
        val n = list.size
        val winnerCount = when {
            n <= 2 -> n
            n == 3 -> 2
            else -> {
                val lastTwo = list.takeLast(2)
                val tieOnSecondToLast = lastTwo[0].place == lastTwo[1].place
                if (tieOnSecondToLast) n - 1 else n - 2
            }
        }
        return list.mapIndexed { i, s -> s.copy(isWinner = i < winnerCount) }
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