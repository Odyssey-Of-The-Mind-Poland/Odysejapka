package odyseja.odysejapka.form.problem.form

import jakarta.persistence.*
import odyseja.odysejapka.form.FormDataConverter
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/**
 * Ogólny arkusz problemowy z kategoriami punktacji i liczbą sędziów w każdym regionie,
 * ale jeszcze nieprzydzielony do konkretnej drużyny.
 */

@Entity
@Table(name = "problem_form")
class ProblemFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(unique = true, nullable = false)
    var problem: Int = 0

    @Column(name = "form_data", columnDefinition = "jsonb", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = FormDataConverter::class)
    var form: FormData? = null
}
