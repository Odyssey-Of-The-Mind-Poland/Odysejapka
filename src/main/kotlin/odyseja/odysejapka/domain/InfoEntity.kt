package odyseja.odysejapka.domain

import javax.persistence.*

@Entity(name = "info")
class InfoEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column()
        val id: Int,
        @Column()
        val infoName: String,
        @Column()
        var infoText: String,
        @ManyToOne(fetch = FetchType.LAZY)
        val city: CityEntity
) {
  fun toInfo() : Info {
    return Info(id, infoName, infoText, city.id)
  }
}