package odyseja.odysejapka.form

data class ProblemForm(
    val dtEntries: List<LongTermFormEntry>,
    val styleEntries: List<LongTermFormEntry>,
    val penaltyEntries: List<LongTermFormEntry>
) {

}
