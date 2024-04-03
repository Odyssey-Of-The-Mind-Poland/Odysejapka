package odyseja.odysejapka.stage

import odyseja.odysejapka.city.CityEntity
import javax.persistence.*

@Entity(name = "stage")
class StageEntity (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column
        val id: Int,
        @Column
        val number: Int,
        @Column
        var name: String,
        @ManyToOne(fetch=FetchType.LAZY)
        var cityEntity: CityEntity
)