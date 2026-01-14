package odyseja.odysejapka.form

import jakarta.persistence.*

@Entity(name = "form_entry")
class FormEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column
    var problem: Int = 0

    @Column
    var name: String = ""

    @Column
    @Enumerated(EnumType.STRING)
    var formCategory: FormCategory = FormCategory.DT

    @Column
    @Enumerated(EnumType.STRING)
    var entryType: EntryType = EntryType.SCORING

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: FormEntryEntity? = null

    @Column
    var orderIndex: Int = 0

    // SCORING fields
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var scoringType: LongTermFormEntry.ScoringType? = null

    @Column(nullable = true)
    var noElementEnabled: Boolean? = null

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var subjectiveRange: SubjectiveRanges? = null

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var objectiveBucket: ObjectiveBuckets? = null

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var judges: LongTermFormEntry.JudgesType? = null

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var styleType: StyleFormEntry.StyleType? = null

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var penaltyValueType: PenaltyValueType? = null

    @Column(nullable = true)
    var penaltyMin: Double? = null

    @Column(nullable = true)
    var penaltyMax: Double? = null

    @Column(nullable = true)
    var penaltySingleValue: Double? = null

    @ElementCollection
    @CollectionTable(name = "form_entry_penalty_discrete_values", joinColumns = [JoinColumn(name = "form_entry_id")])
    @Column(name = "value")
    var penaltyDiscreteValues: List<Double>? = null

    enum class EntryType {
        SCORING, SECTION, SCORING_GROUP, STYLE, PENALTY
    }

    enum class PenaltyValueType {
        RANGE, DISCRETE, SINGLE, ZERO_BALSA
    }

    enum class FormCategory {
        DT, STYLE, PENALTY
    }


    fun toLongTermFormEntry(childrenByParent: Map<Long, List<FormEntryEntity>>): LongTermFormEntry {
        val children = childrenByParent[id] ?: listOf()

        val entryType = when (entryType) {
            EntryType.SCORING -> LongTermFormEntry.EntryType.SCORING
            EntryType.SECTION -> LongTermFormEntry.EntryType.SECTION
            EntryType.SCORING_GROUP -> LongTermFormEntry.EntryType.SCORING_GROUP
            else -> LongTermFormEntry.EntryType.SCORING
        }

        val scoring = when (entryType) {
            LongTermFormEntry.EntryType.SCORING -> {
                scoringType?.let {
                    LongTermFormEntry.ScoringData(
                        scoringType = it,
                        noElementEnabled = noElementEnabled ?: false,
                        judges = judges ?: LongTermFormEntry.JudgesType.A,
                        subjectiveRange = subjectiveRange,
                        objectiveBucket = objectiveBucket
                    )
                }
            }

            else -> null
        }

        return LongTermFormEntry(
            id = id,
            name = name,
            type = entryType,
            scoring = scoring,
            entries = children.map { it.toLongTermFormEntry(childrenByParent) },
            sortIndex = orderIndex
        )
    }

    fun toStyleFormEntry(childrenByParent: Map<Long, List<FormEntryEntity>>): StyleFormEntry {
        val children = childrenByParent[id] ?: listOf()

        return StyleFormEntry(
            id = id,
            name = name,
            type = StyleFormEntry.EntryType.STYLE,
            styleType = styleType ?: StyleFormEntry.StyleType.PREDEFINED,
            entries = children.map { it.toStyleFormEntry(childrenByParent) },
            sortIndex = orderIndex
        )
    }

    fun toPenaltyFormEntry(childrenByParent: Map<Long, List<FormEntryEntity>>): PenaltyFormEntry {
        val children = childrenByParent[id] ?: listOf()

        val penaltyType = when (penaltyValueType) {
            PenaltyValueType.RANGE -> PenaltyFormEntry.PenaltyType.RANGE
            PenaltyValueType.DISCRETE -> PenaltyFormEntry.PenaltyType.DISCRETE
            PenaltyValueType.SINGLE -> PenaltyFormEntry.PenaltyType.SINGLE
            PenaltyValueType.ZERO_BALSA -> PenaltyFormEntry.PenaltyType.ZERO_BALSA
            null -> null
        }

        val penaltyRange = toRangePenaltyData()
        val penaltyDiscrete = toDiscretePenaltyData()
        val penaltySingle = toSinglePenaltyData()

        return PenaltyFormEntry(
            id = id,
            name = name,
            type = PenaltyFormEntry.EntryType.PENALTY,
            penaltyType = penaltyType,
            penaltyRange = penaltyRange,
            penaltyDiscrete = penaltyDiscrete,
            penaltySingle = penaltySingle,
            entries = children.map { it.toPenaltyFormEntry(childrenByParent) },
            sortIndex = orderIndex
        )
    }

    private fun toRangePenaltyData(): PenaltyFormEntry.RangeData? {
        val min = penaltyMin ?: return null
        val max = penaltyMax ?: return null
        return PenaltyFormEntry.RangeData(min, max)
    }

    private fun toDiscretePenaltyData(): PenaltyFormEntry.DiscreteData? {
        val values = penaltyDiscreteValues ?: return null
        return values.takeIf { it.isNotEmpty() }?.let { PenaltyFormEntry.DiscreteData(it) }
    }

    private fun toSinglePenaltyData(): PenaltyFormEntry.SingleData? {
        return penaltySingleValue?.let { PenaltyFormEntry.SingleData(it) }
    }
}