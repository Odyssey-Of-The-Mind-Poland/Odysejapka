package odyseja.odysejapka.domain

import javax.persistence.Column

data class Performance (
  val id: Int,
  val city: String,
  val team: String,
  val problem: Int,
  val age: Int,
  val stage: Int,
  val performance: String,
  val spontan: String,
  val part: Int,
  var performanceDay: String,
  var spontanDay: String
)