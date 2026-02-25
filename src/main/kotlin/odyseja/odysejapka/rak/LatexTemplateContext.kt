package odyseja.odysejapka.rak

/** View model for the LaTeX results template. Template is fully in results.tex; code only fills this. */
data class LatexResultsContext(
    val contestName: String,
    val groups: List<LatexGroup>
)

data class LatexGroup(
    val problem: Int,
    val division: Int,
    val problemName: String,
    val showWeight: Boolean,
    val teamScores: List<LatexRow>
)

data class LatexRow(
    val placeStyle: String,
    val teamStyle: String,
    val cityStyle: String,
    val showWeight: Boolean,
    val weightHeld: String,
    val balsaScore: String,
    val longTermScore: String,
    val styleScore: String,
    val spontaneousScore: String,
    val total: String,
    val rawLongTerm: String,
    val rawStyle: String,
    val rawSpontaneous: String,
    val penaltyStyle: String
)
