package odyseja.odysejapka.form

data class LongTermFormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val scoring: ScoringData? = null,
    val entries: List<LongTermFormEntry> = emptyList(),
    val sortIndex: Int = 0,
) {
    init {
        when (type) {
            EntryType.SCORING -> {
                require(scoring != null) { "Scoring data is required for SCORING type" }
            }
            EntryType.SECTION -> {
                require(scoring == null) { "Scoring data must be null for SECTION type" }
            }
            EntryType.SCORING_GROUP -> {
                require(scoring == null) { "Scoring data must be null for SCORING_GROUP type" }
            }
        }
    }

    enum class EntryType {
        SCORING, SECTION, SCORING_GROUP,
    }

    data class ScoringData(
        val scoringType: ScoringType,
        val noElementEnabled: Boolean,
        val judges: JudgesType = JudgesType.A,
        val subjectiveRange: SubjectiveRanges? = null,
        val objectiveBucket: ObjectiveBuckets? = null
    )

    enum class JudgesType {
        A, B, A_PLUS_B
    }

    enum class ScoringType {
        SUBJECTIVE, OBJECTIVE
    }
}
