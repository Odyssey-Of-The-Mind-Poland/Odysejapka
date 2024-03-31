package odyseja.odysejapka.city

import javax.persistence.*

@Entity(name = "city")
class CityEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column
        val id: Int,
        @Column
        val name: String
)