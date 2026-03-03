package odyseja.odysejapka.spontan

import odyseja.odysejapka.timetable.PerformanceGroup

data class GroupId(
    val problem: Int,
    val age: Int,
    val league: String
)

fun PerformanceGroup.Group.toGroupId() = GroupId(
    problem = problem,
    age = age,
    league = league ?: ""
)
