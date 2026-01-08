package odyseja.odysejapka.form

data class ProblemForm(
    val dtEntries: List<LongTermFormEntry>,
    val styleEntries: List<StyleFormEntry>,
    val penaltyEntries: List<PenaltyFormEntry>
) {

}
