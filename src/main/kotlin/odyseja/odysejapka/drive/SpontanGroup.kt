package odyseja.odysejapka.drive

import odyseja.odysejapka.timetable.Performance

data class SpontanGroups(val group: Group, val performances: List<Performance>) {

    fun groupCode(): String {
        val league = if (group.league.isNotEmpty()) group.league else " "
        return "P${group.problem}G${group.age}${league}"
    }

    data class Group(val problem: Int, val age: Int, val league: String) {

        companion object {
            fun fromPerformance(performance: Performance): Group {
                return Group(performance.problem, performance.age, performance.league)
            }
        }
    }
}
