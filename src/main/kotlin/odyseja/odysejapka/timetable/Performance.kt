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
  var performanceDate: LocalDate = LocalDate.of(2025, 4, 1)
) {

  fun spontanSort(): String {
    val day = if (spontanDay.lowercase().contains("sobota")) "0" else "1"
    return "$day$spontan"
  }

  fun isForeigner(): Boolean {
    for (foreigner in listOf("spain", "china")) {
      if (city.lowercase().contains(foreigner) || team.lowercase().contains(foreigner)) {
        return true
      }
    }
    return false
  }
}