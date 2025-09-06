package odyseja.odysejapka.form

import odyseja.odysejapka.form.FormEntry.CalcType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class FormEntryEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0;

    @Column
    var problem: Int = 0

    @Column
    var name: String = ""

    @Column
    @Enumerated(javax.persistence.EnumType.STRING)
    var calcType: CalcType = CalcType.SUM

    @Column
    @Enumerated(javax.persistence.EnumType.STRING)
    var formCategory: FormEntry.FormCategory = FormEntry.FormCategory.DT

    companion object {
        fun from(problem: Int, entry: FormEntry): FormEntryEntity {
            return FormEntryEntity().apply {
                this.problem = problem
                this.name = entry.name
                this.calcType = entry.calcType
            }
        }
    }

    fun toFormEntry(): FormEntry {
        return FormEntry(id, name, calcType, formCategory)
    }
}