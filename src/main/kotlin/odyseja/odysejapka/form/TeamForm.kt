package odyseja.odysejapka.form

data class TeamForm(
    val performanceId: Int,
    val teamName: String,
    val cityName: String,
    val problem: Int,
    val age: Int,
    val isFo: Boolean,
    val performanceAt: String = "",
    val performanceTime: String = "",
    val dtEntries: List<DtTeamFormEntry>,
    val styleEntries: List<StyleTeamFormEntry>,
    val penaltyEntries: List<PenaltyTeamFormEntry>,
    val weightHeldEntries: List<WeightHeldTeamFormEntry> = emptyList(),
    val validationErrors: List<ValidationFailure> = emptyList(),
    val anomalies: List<Anomaly> = emptyList(),
    val canPreview: Boolean = false,
    val approved: Boolean = false,
    val judgeCount: Int = 1
) {
    data class DtTeamFormEntry(
        val entry: LongTermFormEntry,
        val results: Map<JudgeType, Map<Int, Long?>>,
        val noElement: Boolean = false,
        val noElementComment: String? = null,
        val nestedEntries: List<DtTeamFormEntry> = emptyList()
    )

    data class StyleTeamFormEntry(
        val entry: StyleFormEntry,
        val results: Map<JudgeType, Map<Int, Long?>>,
        val styleName: String? = null
    )

    data class PenaltyTeamFormEntry(
        val entry: PenaltyFormEntry,
        val result: Long?,
        val zeroBalsa: Boolean? = null,
        val comment: String? = null
    )

    data class WeightHeldTeamFormEntry(
        val entry: WeightHeldFormEntry,
        val weights: List<Double> = emptyList()
    )
}
