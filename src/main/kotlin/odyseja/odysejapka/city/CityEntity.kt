package odyseja.odysejapka.city

import jakarta.persistence.*

@Entity(name = "city")
class CityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    var id: Int,
    @Column
    var name: String,
    @Column
    var level: KonkursLevel = KonkursLevel.REGIONAL
)

enum class KonkursLevel {
    FINAL,
    REGIONAL
}