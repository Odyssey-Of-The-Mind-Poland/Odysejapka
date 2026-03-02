package odyseja.odysejapka.spontan

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "spontan_result")
class SpontanResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(name = "performance_id", unique = true, nullable = false)
    var performanceId: Int = 0

    @Column(name = "results", columnDefinition = "jsonb", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = SpontanResultsConverter::class)
    var results: List<SpontanResultEntry>? = null

    @Column(name = "raw_spontan")
    var rawSpontan: Double? = null
}
