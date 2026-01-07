package odyseja.odysejapka.form

data class FormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val scoring: ScoringData? = null,
    val scoringGroup: ScoringGroupData? = null,
    val entries: List<FormEntry> = emptyList()
) {
    init {
        when (type) {
            EntryType.SCORING -> {
                require(scoring != null) { "Scoring data is required for SCORING type" }
                require(scoringGroup == null) { "ScoringGroup data must be null for SCORING type" }
            }
            EntryType.SECTION -> {
                require(scoring == null) { "Scoring data must be null for SECTION type" }
                require(scoringGroup == null) { "ScoringGroup data must be null for SECTION type" }
            }
            EntryType.SCORING_GROUP -> {
                require(scoringGroup != null) { "ScoringGroup data is required for SCORING_GROUP type" }
                require(scoring == null) { "Scoring data must be null for SCORING_GROUP type" }
            }
        }
    }

    enum class EntryType {
        SCORING, SECTION, SCORING_GROUP
    }

    data class ScoringData(
        val scoringType: ScoringType,
        val pointsMin: Int,
        val pointsMax: Int,
        val judges: JudgeType,
        val noElement: Boolean
    )

    data class ScoringGroupData(
        val pointsMin: Int,
        val pointsMax: Int
    )

    enum class ScoringType {
        SUBJECTIVE, OBJECTIVE
    }

    enum class JudgeType {
        A, B, A_PLUS_B
    }
}

enum class CalcType {
    SUM, AVERAGE
}
