package odyseja.odysejapka.info

import javax.persistence.*

@Entity(name = "info_category")
class InfoCategoryEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column()
  val id: Int,
  @Column()
  val name: String
)

