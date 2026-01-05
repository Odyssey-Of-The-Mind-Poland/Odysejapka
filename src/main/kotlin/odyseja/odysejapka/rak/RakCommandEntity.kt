package odyseja.odysejapka.rak

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository

@Entity
class RakCommandEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val cityId: Int? = null,

    val zspId: String
)

interface RakCommandRepository : JpaRepository<RakCommandEntity, Long> {
    fun findFirstByOrderByIdDesc(): RakCommandEntity?
    fun findFirstByCityIdOrderByIdDesc(cityId: Int): RakCommandEntity?
}


