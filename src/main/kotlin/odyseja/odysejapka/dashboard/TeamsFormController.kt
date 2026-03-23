package odyseja.odysejapka.dashboard

import odyseja.odysejapka.form.TeamResultEntity
import odyseja.odysejapka.form.TeamResultService
import odyseja.odysejapka.spontan.SpontanResultEntity
import odyseja.odysejapka.spontan.SpontanResultRepository
import odyseja.odysejapka.timetable.PerformanceGroup
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/dashboard/teams")
class TeamsFormController(
    private val performanceGroupService: odyseja.odysejapka.timetable.PerformanceGroupService,
    private val cityAccessService: CityAccessService,
    private val stageAccessService: StageAccessService,
    private val userAccessService: UserAccessService,
    private val teamResultService: TeamResultService,
    private val spontanResultRepository: SpontanResultRepository
) {

    @GetMapping
    fun getTeams(@AuthenticationPrincipal principal: Any?): List<TeamListGroup> {
        val userId = extractUserId(principal) ?: return emptyList()

        val accessibleCities = cityAccessService.getAccessibleCities(userId)
        if (accessibleCities.isEmpty()) return emptyList()

        val allGroups = accessibleCities.flatMap { performanceGroupService.getPerformanceGroups(it.id) }
        val filteredGroups = filterByAccess(allGroups, accessibleCities, userId)
        val performanceIds = filteredGroups.flatMap { g -> g.performances.map { it.id } }

        val teamResults = getTeamResults(performanceIds)
        val spontanResults = getSpontanResults(performanceIds)

        return filteredGroups.map { group ->
            TeamListGroup(
                group = group.group,
                performances = group.performances.map { p ->
                    TeamListPerformance.from(p, teamResults[p.id], spontanResults[p.id])
                }
            )
        }
    }

    private fun filterByAccess(
        groups: List<PerformanceGroup>,
        accessibleCities: List<odyseja.odysejapka.city.CityEntity>,
        userId: String
    ): List<PerformanceGroup> {
        if (userAccessService.isAdmin()) return groups

        val stagesByCityId = accessibleCities.associate { it.id to stageAccessService.getAccessibleStages(userId, it.id) }

        return groups.filter { group ->
            if (userAccessService.hasProblemRole(group.group.problem)) return@filter true
            val cityId = accessibleCities.find { it.name == group.group.city }?.id ?: return@filter false
            group.group.stage in (stagesByCityId[cityId] ?: return@filter false)
        }
    }

    private fun getTeamResults(performanceIds: List<Int>): Map<Int, TeamResultEntity> {
        if (performanceIds.isEmpty()) return emptyMap()
        return teamResultService.getTeamResults(performanceIds).associateBy { it.performanceId }
    }

    private fun getSpontanResults(performanceIds: List<Int>): Map<Int, SpontanResultEntity> {
        if (performanceIds.isEmpty()) return emptyMap()
        return spontanResultRepository.findAllByPerformanceIdIn(performanceIds).associateBy { it.performanceId }
    }
}
