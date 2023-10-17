package odyseja.odysejapka.domain

import javax.persistence.*

@Entity(name = "sponsor")
class SponsorEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  val id: Int,
  @Lob
  @Column(length = 10000)
  val image: ByteArray,
  @Column
  val rowIndex: Int,
  @Column
  @GeneratedValue(strategy = GenerationType.AUTO)
  val columnIndex: Int
) {

  fun toSponsor(): Sponsor {
    return Sponsor(id, rowIndex, columnIndex)
  }
}