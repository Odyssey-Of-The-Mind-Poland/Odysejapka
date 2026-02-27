package odyseja.odysejapka.form

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "team_results")
class TeamResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(name = "performance_id", unique = true, nullable = false)
    var performanceId: Int = 0

    @Column(name = "results", columnDefinition = "jsonb", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = PerformanceResultsConverter::class)
    var results: PerformanceResultsRequest? = null

    @Column(nullable = false)
    var approved: Boolean = false

    @Column(name = "performance_at")
    var performanceAt: String? = null

    @Column(name = "performance_time")
    var performanceTime: String? = null

    @Column(name = "form_state", columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    var formState: FormState = FormState.NOT_SCORED
}

