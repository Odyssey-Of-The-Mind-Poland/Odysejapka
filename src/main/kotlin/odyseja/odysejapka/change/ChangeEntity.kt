package odyseja.odysejapka.change

import java.sql.Timestamp
import jakarta.persistence.*

@Entity(name = "change")
class ChangeEntity (
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  var id: Int,
  @Column
  var changedAt: Timestamp
)