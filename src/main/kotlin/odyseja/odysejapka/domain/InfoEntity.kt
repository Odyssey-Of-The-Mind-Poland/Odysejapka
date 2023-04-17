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
  val city: CityEntity,
  @ManyToOne(fetch = FetchType.LAZY)
  val category: InfoCategoryEntity,
  @Column()
  var sortNumber: Int
) {
  fun toInfo(): Info {
    return Info(id, infoName, infoText, city.id, category.id, sortNumber, category.name)
  }
}