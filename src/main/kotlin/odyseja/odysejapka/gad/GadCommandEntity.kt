package odyseja.odysejapka.gad

import org.springframework.data.jpa.repository.JpaRepository
import jakarta.persistence.*

@Entity
class GadCommandEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(columnDefinition = "TEXT")
    val jsonData: String
)

interface GadCommandRepository : JpaRepository<GadCommandEntity, Long> {
    fun findFirstByOrderByIdDesc(): GadCommandEntity?
}
