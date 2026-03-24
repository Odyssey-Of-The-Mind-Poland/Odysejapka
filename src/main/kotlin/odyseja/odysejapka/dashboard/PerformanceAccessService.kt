package odyseja.odysejapka.dashboard

import odyseja.odysejapka.exceptions.NoAccessException
import odyseja.odysejapka.stage.StageUserService
import odyseja.odysejapka.timetable.TimeTableService
import odyseja.odysejapka.users.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PerformanceAccessService(
    private val timeTableService: TimeTableService,
    private val userAccessService: UserAccessService,
    private val userService: UserService,
    private val stageUserService: StageUserService
) {

    @Transactional(readOnly = true)
    fun checkAccess(principalUserId: String, performanceId: Int) {
        if (userAccessService.isAdmin()) return

        val performance = timeTableService.getPerformanceEntity(performanceId)

        if (userAccessService.hasProblemRole(performance.problemEntity.id)) return

        val user = userService.getUserEntityOrNullByUserId(principalUserId)
            ?: throw NoAccessException("Brak uprawnień do wyświetlania przedstawień")
        val stageUser = stageUserService.getStageUserOrNullByUserId(user.id!!)
            ?: throw NoAccessException("Brak uprawnień do wyświetlania przedstawień")

        if (stageUser.cityId != performance.cityEntity.id || stageUser.stage != performance.stageEntity.number) {
            throw NoAccessException("Brak uprawnień do wyświetlenia tego przedstawienia")
        }
    }
}
