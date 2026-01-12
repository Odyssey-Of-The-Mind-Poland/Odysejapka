package odyseja.odysejapka.form

data class ProblemForm(
    val dtEntries: List<LongTermFormEntry>,
    val styleEntries: List<StyleFormEntry>,
    val penaltyEntries: List<PenaltyFormEntry>,
    val smallJudgesTeam: List<Int>? = null,
    val bigJudgesTeam: List<Int>? = null
) {

}
