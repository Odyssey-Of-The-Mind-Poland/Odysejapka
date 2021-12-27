package odyseja.odysejapka.domain

import javax.persistence.*

@Entity(name = "sponsor")
class SponsorEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  val id: Int,
  @Column
  val name: String,
  @Lob
  @Column(length = 10000)
  val image: ByteArray
)