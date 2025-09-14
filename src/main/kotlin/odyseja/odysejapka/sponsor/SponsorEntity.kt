package odyseja.odysejapka.sponsor

import odyseja.odysejapka.city.CityEntity
import jakarta.persistence.*

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
  val rowOrder: Int,
  @Column
  val columnOrder: Int,
  @ManyToOne(fetch = FetchType.LAZY)
  val city: CityEntity
) {

  fun toSponsor(): Sponsor {
    return Sponsor(id, rowOrder, columnOrder)
  }
}