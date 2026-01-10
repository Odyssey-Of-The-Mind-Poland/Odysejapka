package odyseja.odysejapka.sak

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository

@Entity
class SakCommandEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val cityId: Int? = null,

    @Column(columnDefinition = "TEXT")
    val jsonData: String
)

interface SakCommandRepository : JpaRepository<SakCommandEntity, Long> {
    fun findFirstByOrderByIdDesc(): SakCommandEntity?
    fun findFirstByCityIdOrderByIdDesc(cityId: Int): SakCommandEntity?
}


