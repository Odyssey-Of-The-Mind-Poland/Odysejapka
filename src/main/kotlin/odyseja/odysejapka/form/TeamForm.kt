package odyseja.odysejapka.form

data class TeamForm(
    val performanceId: Int,
    val dtEntries: List<DtTeamFormEntry>,
    val styleEntries: List<StyleTeamFormEntry>,
    val penaltyEntries: List<PenaltyTeamFormEntry>
) {
    data class DtTeamFormEntry(
        val entry: LongTermFormEntry,
        val judgesA: Map<Int, Long?>,
        val judgesB: Map<Int, Long?>
    )
    
    data class StyleTeamFormEntry(
        val entry: StyleFormEntry,
        val styleJudge: Map<Int, Long?>
    )
    
    data class PenaltyTeamFormEntry(
        val entry: PenaltyFormEntry,
        val penalty: Map<Int, Long?>
    )
}
