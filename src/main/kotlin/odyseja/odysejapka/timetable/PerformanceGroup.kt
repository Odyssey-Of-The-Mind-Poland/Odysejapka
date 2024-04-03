package odyseja.odysejapka.timetable

data class PerformanceGroup(
    val group: Group,
    val performances: List<Performance>
) {

  data class Group(val city: String,
                   val problem: Int,
                   val age: Int,
                   val stage: Int,
                   val part: Int,
                   val league: String)
}
