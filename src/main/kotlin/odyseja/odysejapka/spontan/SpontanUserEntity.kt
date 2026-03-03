package odyseja.odysejapka.spontan

import jakarta.persistence.*

@Entity
@Table(name = "spontan_users")
class SpontanUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "city_id", nullable = false)
    val cityId: Int,

    @Column(name = "user_id", nullable = false)
    val userId: Long
)
