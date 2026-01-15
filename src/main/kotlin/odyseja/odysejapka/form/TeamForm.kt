package odyseja.odysejapka.form

data class TeamForm(
    val performanceId: Int,
    val teamName: String,
    val cityName: String,
    val problem: Int,
    val age: Int,
    val isFo: Boolean,
    val dtEntries: List<DtTeamFormEntry>,
    val styleEntries: List<StyleTeamFormEntry>,
    val penaltyEntries: List<PenaltyTeamFormEntry>
) {
    data class DtTeamFormEntry(
        val entry: LongTermFormEntry,
        val results: Map<JudgeType, Map<Int, Long?>>,
        val noElement: Boolean = false
    )
    
    data class StyleTeamFormEntry(
        val entry: StyleFormEntry,
        val results: Map<JudgeType, Map<Int, Long?>>
    )
    
    data class PenaltyTeamFormEntry(
        val entry: PenaltyFormEntry,
        val result: Long?
    )
}
