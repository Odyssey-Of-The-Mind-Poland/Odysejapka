package odyseja.odysejapka.dashboard

import odyseja.odysejapka.form.TeamResultRepository
import odyseja.odysejapka.timetable.PerformanceGroupService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/dashboard/teams")
class TeamsFormController(
    private val performanceGroupService: PerformanceGroupService,
    private val cityAccessService: CityAccessService,
    private val stageAccessService: StageAccessService,
    private val userAccessService: UserAccessService,
    private val teamResultRepository: TeamResultRepository
) {

    @GetMapping
    fun getTeams(@AuthenticationPrincipal principal: Any?): List<TeamListGroup> {
        val userId = extractUserId(principal) ?: return emptyList()

        val accessibleCities = cityAccessService.getAccessibleCities(userId)
        if (accessibleCities.isEmpty()) return emptyList()

        val allGroups = accessibleCities.flatMap { city ->
            performanceGroupService.getPerformanceGroups(city.id)
        }

        val filteredGroups = if (userAccessService.isAdmin()) {
            allGroups
        } else {
            val accessibleStagesByCityId = accessibleCities.associate { city ->
                city.id to stageAccessService.getAccessibleStages(userId, city.id)
            }

            allGroups.filter { group ->
                if (userAccessService.hasProblemRole(group.group.problem)) return@filter true

                val cityEntity = accessibleCities.find { it.name == group.group.city }
                    ?: return@filter false
                val stages = accessibleStagesByCityId[cityEntity.id] ?: return@filter false
                group.group.stage in stages
            }
        }

        val allPerformanceIds = filteredGroups.flatMap { group ->
            group.performances.map { it.id }
        }

        val actualTimesMap = if (allPerformanceIds.isNotEmpty()) {
            teamResultRepository.findAllByPerformanceIdIn(allPerformanceIds)
                .associate { it.performanceId to it.performanceAt }
        } else {
            emptyMap()
        }

        return filteredGroups.map { group ->
            TeamListGroup(
                group = group.group,
                performances = group.performances.map { performance ->
                    TeamListPerformance.from(performance, actualTimesMap[performance.id])
                }
            )
        }
    }
}
