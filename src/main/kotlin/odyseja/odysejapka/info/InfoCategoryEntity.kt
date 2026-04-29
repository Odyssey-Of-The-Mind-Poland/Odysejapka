package odyseja.odysejapka.info

import jakarta.persistence.*

@Entity(name = "info_category")
class InfoCategoryEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column()
  var id: Int,
  @Column()
  var name: String
) {
  fun toInfoCategory(): InfoCategory {
    return InfoCategory(id, name)
  }
}

