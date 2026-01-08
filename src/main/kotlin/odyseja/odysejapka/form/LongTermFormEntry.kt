package odyseja.odysejapka.form

data class LongTermFormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val scoring: ScoringData? = null,
    val scoringGroup: ScoringGroupData? = null,
    val entries: List<LongTermFormEntry> = emptyList()
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
            EntryType.STYLE -> {
                require(scoring == null) { "Scoring data must be null for STYLE type" }
                require(scoringGroup == null) { "ScoringGroup data must be null for STYLE type" }
            }
            EntryType.PENALTY -> {
                require(scoring == null) { "Scoring data must be null for PENALTY type" }
                require(scoringGroup == null) { "ScoringGroup data must be null for PENALTY type" }
            }
        }
    }

    enum class EntryType {
        SCORING, SECTION, SCORING_GROUP, STYLE, PENALTY
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
            this.orderIndex = orderIndex
            this.entryType = when (this@LongTermFormEntry.type) {
                EntryType.SCORING -> FormEntryEntity.EntryType.SCORING
                EntryType.SECTION -> FormEntryEntity.EntryType.SECTION
                EntryType.SCORING_GROUP -> FormEntryEntity.EntryType.SCORING_GROUP
                EntryType.STYLE -> FormEntryEntity.EntryType.STYLE
                EntryType.PENALTY -> FormEntryEntity.EntryType.PENALTY
            }
            when (this@LongTermFormEntry.type) {
                EntryType.SCORING -> scoring?.let {
                    this.scoringType = it.scoringType
                    this.pointsMin = it.pointsMin
                    this.pointsMax = it.pointsMax
                    this.judges = it.judges
                    this.noElement = it.noElement
                }
                EntryType.SCORING_GROUP -> scoringGroup?.let {
                    this.pointsMin = it.pointsMin
                    this.pointsMax = it.pointsMax
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
