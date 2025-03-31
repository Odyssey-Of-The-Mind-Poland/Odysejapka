package odyseja.odysejapka.timetable

import java.time.LocalDate

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
  var spontanDay: String,
  var league: String,
  var zspRow: Int?,
  var zspSheet: String?,
  var performanceDate: LocalDate?
) {


}