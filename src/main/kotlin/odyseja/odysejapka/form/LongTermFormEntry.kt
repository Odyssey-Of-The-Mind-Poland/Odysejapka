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
        val subjectiveRange: SubjectiveRanges? = null,
        val objectiveBucket: ObjectiveBuckets? = null
    )

    enum class ScoringType {
        SUBJECTIVE, OBJECTIVE
    }

    fun toEntity(
        problem: Int,
        category: FormEntryEntity.FormCategory,
        parent: FormEntryEntity?,
        orderIndex: Int,
        existing: FormEntryEntity? = null
    ): FormEntryEntity {
        val entity = existing ?: FormEntryEntity()
        entity.apply {
            this.problem = problem
            this.name = this@LongTermFormEntry.name
            this.formCategory = category
            this.parent = parent
            this.orderIndex = this@LongTermFormEntry.sortIndex
            this.entryType = when (this@LongTermFormEntry.type) {
                EntryType.SCORING -> FormEntryEntity.EntryType.SCORING
                EntryType.SECTION -> FormEntryEntity.EntryType.SECTION
                EntryType.SCORING_GROUP -> FormEntryEntity.EntryType.SCORING_GROUP
            }
            when (this@LongTermFormEntry.type) {
                EntryType.SCORING -> scoring?.let {
                    this.scoringType = it.scoringType
                    this.noElementEnabled = it.noElementEnabled
                    this.subjectiveRange = it.subjectiveRange
                    this.objectiveBucket = it.objectiveBucket
                }
                else -> {}
            }
        }
        return entity
    }
}

enum class CalcType {
    SUM, AVERAGE
}
