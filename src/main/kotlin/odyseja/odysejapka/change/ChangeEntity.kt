package odyseja.odysejapka.change

import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "change")
class ChangeEntity (
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  val id: Int,
  @Column
  val changedAt: Timestamp
)