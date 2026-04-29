package odyseja.odysejapka.info

import odyseja.odysejapka.city.CityEntity
import jakarta.persistence.*

@Entity(name = "info")
class InfoEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column()
  var id: Int,
  @Column()
  var infoName: String,
  @Column(columnDefinition = "TEXT")
  var infoText: String,
  @ManyToOne(fetch = FetchType.LAZY)
  var city: CityEntity,
  @ManyToOne(fetch = FetchType.LAZY)
  var category: InfoCategoryEntity,
  @Column()
  var sortNumber: Int,
  @Column(columnDefinition = "TEXT")
  var icon: String? = null,
  @Column()
  var color: String? = null
) {
  fun toInfo(): Info {
    return Info(id, infoName, infoText, city.id, category.id, sortNumber, category.name, icon, color)
  }
}