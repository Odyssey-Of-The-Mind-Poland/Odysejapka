package odyseja.odysejapka.form

import odyseja.odysejapka.form.FormEntry.CalcType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class FormEntryEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0;

    @Column
    var problem: Int = 0

    @Column
    var name: String = ""

    @Column
    @Enumerated(jakarta.persistence.EnumType.STRING)
    var calcType: CalcType = CalcType.SUM

    @Column
    @Enumerated(jakarta.persistence.EnumType.STRING)
    var formCategory: FormCategory = FormCategory.DT

    companion object {
        fun from(problem: Int, entry: FormEntry, category: FormCategory): FormEntryEntity {
            return FormEntryEntity().apply {
                this.problem = problem
                this.name = entry.name
                this.calcType = entry.calcType
                this.formCategory = category
            }
        }
    }

    fun toFormEntry(): FormEntry {
        return FormEntry(id, name, calcType)
    }

    enum class FormCategory {
        DT, STYLE, PENALTY
    }
}