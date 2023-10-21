package odyseja.odysejapka.planner

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable.PlanningVariable

@PlanningEntity
class Performance(
    @PlanningId
    var id: Long,
    var teamName: String,
    @PlanningVariable
    var timeSlot: TimeSlot?,
    @PlanningVariable
    var stage: PlannerStage?
) {

    constructor(id: Long, teamName: String) : this(id, teamName, null, null)

    constructor() : this(0, "", null, null)
}