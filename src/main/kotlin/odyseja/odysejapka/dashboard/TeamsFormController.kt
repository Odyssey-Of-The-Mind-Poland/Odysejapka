package odyseja.odysejapka.dashboard

import odyseja.odysejapka.timetable.PerformanceGroup
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
    private val userAccessService: UserAccessService
) {

    @GetMapping
    fun getTeams(@AuthenticationPrincipal principal: Any?): List<PerformanceGroup> {
        val userId = extractUserId(principal) ?: return emptyList()

        val accessibleCities = cityAccessService.getAccessibleCities(userId)
        if (accessibleCities.isEmpty()) return emptyList()

        val allGroups = accessibleCities.flatMap { city ->
            performanceGroupService.getPerformanceGroups(city.id)
        }

        if (userAccessService.isAdmin()) return allGroups

        val accessibleStagesByCityId = accessibleCities.associate { city ->
            city.id to stageAccessService.getAccessibleStages(userId, city.id)
        }

        return allGroups.filter { group ->
            if (userAccessService.hasProblemRole(group.group.problem)) return@filter true

            val cityEntity = accessibleCities.find { it.name == group.group.city }
                ?: return@filter false
            val stages = accessibleStagesByCityId[cityEntity.id] ?: return@filter false
            group.group.stage in stages
        }
    }
}
