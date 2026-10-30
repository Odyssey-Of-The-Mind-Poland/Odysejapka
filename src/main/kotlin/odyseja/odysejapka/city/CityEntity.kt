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
    // Nullable only because rows created before the column existed have no value; see [applyDefaultLevel].
    @Column
    var level: KonkursLevel? = KonkursLevel.REGIONAL
) {
    @PostLoad
    fun applyDefaultLevel() {
        if (level == null) level = KonkursLevel.REGIONAL
    }
}

enum class KonkursLevel {
    FINAL,
    REGIONAL
}