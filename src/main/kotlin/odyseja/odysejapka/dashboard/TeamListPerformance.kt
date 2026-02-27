package odyseja.odysejapka.dashboard

import odyseja.odysejapka.timetable.Performance
import odyseja.odysejapka.timetable.PerformanceGroup

data class TeamListPerformance(
    val id: Int,
    val city: String,
    val team: String,
    val problem: Int,
    val age: Int,
    val stage: Int,
    val performance: String,
    val performanceDay: String,
    val league: String?,
    val actualPerformanceAt: String? = null,
) {
    companion object {
        fun from(performance: Performance, actualPerformanceAt: String?): TeamListPerformance {
            return TeamListPerformance(
                id = performance.id,
                city = performance.city,
                team = performance.team,
                problem = performance.problem,
                age = performance.age,
                stage = performance.stage,
                performance = performance.performance,
                performanceDay = performance.performanceDay,
                league = performance.league,
                actualPerformanceAt = actualPerformanceAt,
            )
        }
    }
}

data class TeamListGroup(
    val group: PerformanceGroup.Group,
    val performances: List<TeamListPerformance>
)
