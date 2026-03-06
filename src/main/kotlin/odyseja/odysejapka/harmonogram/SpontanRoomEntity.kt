package odyseja.odysejapka.harmonogram

import odyseja.odysejapka.city.CityEntity
import jakarta.persistence.*

@Entity(name = "spontan_room")
class SpontanRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val id: Int = 0,
    @Column
    var name: String,
    @ManyToOne(fetch = FetchType.LAZY)
    var cityEntity: CityEntity
)
