package odyseja.odysejapka.form

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "form_problem_json")
class FormProblemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(unique = true, nullable = false)
    var problem: Int = 0

    @Column(name = "form_data", columnDefinition = "jsonb", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = ProblemFormConverter::class)
    var form: ProblemForm? = null
}
