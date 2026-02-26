package odyseja.odysejapka.stage

import jakarta.persistence.*

@Entity
@Table(name = "stage_users")
class StageUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "city_id", nullable = false)
    val cityId: Int,

    @Column(nullable = false)
    val stage: Int,

    @Column(name = "user_id", nullable = false)
    val userId: Long
)
