package odyseja.odysejapka.sponsor

import odyseja.odysejapka.city.CityEntity
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

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
  @OnDelete(action = OnDeleteAction.CASCADE)
  val city: CityEntity
) {

  fun toSponsor(): Sponsor {
    return Sponsor(id, rowOrder, columnOrder)
  }
}