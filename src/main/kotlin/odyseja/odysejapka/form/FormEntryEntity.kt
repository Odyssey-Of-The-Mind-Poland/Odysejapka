package odyseja.odysejapka.form

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class FormEntryEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column
    var problem: Int = 0

    @Column
    var name: String = ""

    @Column(nullable = true)
    @Enumerated(jakarta.persistence.EnumType.STRING)
    var calcType: CalcType? = null

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

    enum class EntryType {
        PUNCTUATION, SECTION, PUNCTUATION_GROUP
    }

    enum class FormCategory {
        DT, STYLE, PENALTY
    }
}