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
    @Enumerated(jakarta.persistence.EnumType.STRING)
    var formCategory: FormCategory = FormCategory.DT

    @Column
    @Enumerated(jakarta.persistence.EnumType.STRING)
    var entryType: EntryType = EntryType.SCORING

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: FormEntryEntity? = null

    @Column
    var orderIndex: Int = 0

    // SCORING fields
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var scoringType: FormEntry.ScoringType? = null

    @Column(nullable = true)
    var pointsMin: Int? = null

    @Column(nullable = true)
    var pointsMax: Int? = null

    @Column(nullable = true)
    @Enumerated(jakarta.persistence.EnumType.STRING)
    var judges: FormEntry.JudgeType? = null

    @Column(nullable = true)
    var noElement: Boolean? = null

    enum class EntryType {
        SCORING, SECTION, SCORING_GROUP, STYLE
    }

    enum class FormCategory {
        DT, STYLE, PENALTY
    }


    fun toFormEntry(childrenByParent: Map<Long, List<FormEntryEntity>>): FormEntry {
        val children = childrenByParent[id] ?: listOf()

        val entryType = when (entryType) {
            EntryType.SCORING -> FormEntry.EntryType.SCORING
            EntryType.SECTION -> FormEntry.EntryType.SECTION
            EntryType.SCORING_GROUP -> FormEntry.EntryType.SCORING_GROUP
            EntryType.STYLE -> FormEntry.EntryType.STYLE
        }

        val scoring = when (entryType) {
            FormEntry.EntryType.SCORING -> {
                scoringType?.let {
                    FormEntry.ScoringData(
                        scoringType = it,
                        pointsMin = pointsMin ?: 0,
                        pointsMax = pointsMax ?: 0,
                        judges = judges ?: FormEntry.JudgeType.A,
                        noElement = noElement ?: false
                    )
                }
            }

            else -> null
        }

        val scoringGroup = when (entryType) {
            FormEntry.EntryType.SCORING_GROUP -> {
                pointsMin?.let { min ->
                    pointsMax?.let { max ->
                        FormEntry.ScoringGroupData(
                            pointsMin = min,
                            pointsMax = max
                        )
                    }
                }
            }

            else -> null
        }

        return FormEntry(
            id = id,
            name = name,
            type = entryType,
            scoring = scoring,
            scoringGroup = scoringGroup,
            entries = children.map { it.toFormEntry(childrenByParent) }
        )
    }
}