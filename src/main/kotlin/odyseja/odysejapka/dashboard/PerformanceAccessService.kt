package odyseja.odysejapka.dashboard

import odyseja.odysejapka.exceptions.NoAccessException
import odyseja.odysejapka.stage.StageUserService
import odyseja.odysejapka.timetable.TimeTableService
import odyseja.odysejapka.users.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PerformanceAccessService(
    private val timeTableService: TimeTableService,
    private val userAccessService: UserAccessService,
    private val userRepository: UserRepository,
    private val stageUserService: StageUserService
) {

    @Transactional(readOnly = true)
    fun checkAccess(principalUserId: String, performanceId: Int) {
        if (userAccessService.isAdmin()) return

        val performance = timeTableService.getPerformanceEntity(performanceId)

        if (userAccessService.hasProblemRole(performance.problemEntity.id)) return

        val user = userRepository.findByUserId(principalUserId)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val stageUser = stageUserService.getStageUserOrNullByUserId(user.id!!)
            ?: throw NoAccessException("Brak uprawnień do wyświetlania przedstawień")

        if (stageUser.cityId != performance.cityEntity.id || stageUser.stage != performance.stageEntity.number) {
            throw NoAccessException("Brak uprawnień do wyświetlenia tego przedstawienia")
        }
    }
}
