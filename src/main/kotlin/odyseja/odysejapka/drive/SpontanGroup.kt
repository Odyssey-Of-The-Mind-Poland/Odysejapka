package odyseja.odysejapka.drive

import odyseja.odysejapka.domain.Performance

data class SpontanGroups(val group: Group, val performances: List<Performance>) {

    fun groupCode(): String {
        return "P${group.problem}A${group.age}"
    }

    data class Group(val problem: Int, val age: Int) {

        companion object {
            fun fromPerformance(performance: Performance): Group {
                return Group(performance.problem, performance.age)
            }
        }
    }
}
