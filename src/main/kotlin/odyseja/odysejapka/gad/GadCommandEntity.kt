package odyseja.odysejapka.gad

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository

@Entity
class GadCommandEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val cityId: Int? = null,

    @Column(columnDefinition = "TEXT")
    val jsonData: String
)

interface GadCommandRepository : JpaRepository<GadCommandEntity, Long> {
    fun findFirstByOrderByIdDesc(): GadCommandEntity?
    fun findFirstByCityIdOrderByIdDesc(cityId: Int): GadCommandEntity?
}
