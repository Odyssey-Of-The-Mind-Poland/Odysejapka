package odyseja.odysejapka.gad

import Team

data class TeamsGroups(val groups: List<TeamsGroup>) {

    companion object {
        fun fromTeams(teams: List<Team>): TeamsGroups {
            val grouped = teams
                .groupBy { it.getGroup() }
                .map { TeamsGroup(it.key, it.value) }
            return TeamsGroups(grouped)
        }
    }
}
