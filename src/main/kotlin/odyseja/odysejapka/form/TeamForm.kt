package odyseja.odysejapka.form

data class TeamForm(
    val performanceId: Int,
    val dtEntries: List<TeamFormEntry<LongTermFormEntry>>,
    val styleEntries: List<TeamFormEntry<StyleFormEntry>>,
    val penaltyEntries: List<TeamFormEntry<PenaltyFormEntry>>
) {
    data class TeamFormEntry<T>(
        val entry: T,
        val judgeResults: Map<Int, Long>
    )
}
