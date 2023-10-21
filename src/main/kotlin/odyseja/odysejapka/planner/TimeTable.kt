package odyseja.odysejapka.planner

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import java.time.LocalTime

@PlanningSolution
class TimeTable(
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    var timeSlots: List<TimeSlot>,
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    var stages: List<PlannerStage>,
    @PlanningEntityCollectionProperty
    var teams: List<Performance>,
    @PlanningScore
    var score: HardSoftScore
) {

    constructor(timeSlots: List<TimeSlot>, stages: List<PlannerStage>, teams: List<Performance>) : this(
        timeSlots,
        stages,
        teams,
        HardSoftScore.ZERO
    )

    constructor() : this(emptyList(), emptyList(), emptyList(), HardSoftScore.ZERO)
}

data class TimeSlot(var startTime: LocalTime, var endTime: LocalTime)

data class PlannerStage(var name: String)