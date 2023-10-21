package odyseja.odysejapka.planner

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable.PlanningVariable

@PlanningEntity
class Performance(
    @PlanningId
    var id: Long,
    var teamName: String,
    var age: Int,
    var problem: Int?,
    @PlanningVariable
    var timeSlot: TimeSlot?,
    @PlanningVariable
    var stage: PlannerStage?
) {

    constructor(id: Long, teamName: String, age: Int, stage: Int, problem: Int?) : this(
        id,
        teamName,
        0,
        problem,
        null,
        null
    )

    constructor() : this(0, "", 0, 0, null, null)
}