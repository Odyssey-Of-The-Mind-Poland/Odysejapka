package odyseja.odysejapka.dashboard

import odyseja.odysejapka.form.FormState
import odyseja.odysejapka.form.TeamResultEntity
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
    val formState: String = FormState.NOT_SCORED.name,
    val formStateLabel: String = FormState.NOT_SCORED.label,
) {
    companion object {
        fun from(performance: Performance, teamResult: TeamResultEntity?): TeamListPerformance {
            val state = teamResult?.formState ?: FormState.NOT_SCORED
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
                actualPerformanceAt = teamResult?.performanceAt,
                formState = state.name,
                formStateLabel = state.label,
            )
        }
    }
}

data class TeamListGroup(
    val group: PerformanceGroup.Group,
    val performances: List<TeamListPerformance>
)
