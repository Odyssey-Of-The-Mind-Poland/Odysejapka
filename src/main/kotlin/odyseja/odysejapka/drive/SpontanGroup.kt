package odyseja.odysejapka.drive

import Team
import odyseja.odysejapka.timetable.Performance

data class SpontanGroups(val group: Group, val performances: List<Team>) {

    fun groupCode(): String {
        val league = if (group.league.isNotEmpty()) group.league else " "
        return "P${group.problem}G${group.age}${league}"
    }

    data class Group(val problem: String, val age: String, val league: String) {

        companion object {
            fun fromTeam(team: Team): Group {
                return Group(team.getProblem(), team.getAge(), team.league)
            }
        }
    }
}
