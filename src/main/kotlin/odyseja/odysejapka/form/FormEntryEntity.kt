package odyseja.odysejapka.form

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import kotlin.collections.emptyList
import kotlin.collections.map

@Entity
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
    var entryType: EntryType = EntryType.PUNCTUATION

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: FormEntryEntity? = null

    @Column
    var orderIndex: Int = 0

    // PUNCTUATION fields
    @Column(nullable = true)
    @Enumerated(jakarta.persistence.EnumType.STRING)
    var punctuationType: FormEntry.PunctuationType? = null

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
        PUNCTUATION, SECTION, PUNCTUATION_GROUP
    }

    enum class FormCategory {
        DT, STYLE, PENALTY
    }


    fun toFormEntry(childrenByParent: Map<Long, List<FormEntryEntity>>): FormEntry {
        val children = childrenByParent[id] ?: listOf()

        val entryType = when (entryType) {
            EntryType.PUNCTUATION -> FormEntry.EntryType.PUNCTUATION
            EntryType.SECTION -> FormEntry.EntryType.SECTION
            EntryType.PUNCTUATION_GROUP -> FormEntry.EntryType.PUNCTUATION_GROUP
        }

        val punctuation = when (entryType) {
            FormEntry.EntryType.PUNCTUATION -> {
                punctuationType?.let {
                    FormEntry.PunctuationData(
                        punctuationType = it,
                        pointsMin = pointsMin ?: 0,
                        pointsMax = pointsMax ?: 0,
                        judges = judges ?: FormEntry.JudgeType.A,
                        noElement = noElement ?: false
                    )
                }
            }

            else -> null
        }

        val punctuationGroup = when (entryType) {
            FormEntry.EntryType.PUNCTUATION_GROUP -> {
                pointsMin?.let { min ->
                    pointsMax?.let { max ->
                        FormEntry.PunctuationGroupData(
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
            punctuation = punctuation,
            punctuationGroup = punctuationGroup,
            entries = children.map { it.toFormEntry(childrenByParent) }
        )
    }
}