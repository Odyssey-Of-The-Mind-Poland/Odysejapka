package odyseja.odysejapka.dashboard

import odyseja.odysejapka.stage.StageUserRepository
import odyseja.odysejapka.timetable.PerformanceRepository
import odyseja.odysejapka.users.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class PerformanceAccessService(
    private val performanceRepository: PerformanceRepository,
    private val userAccessService: UserAccessService,
    private val userRepository: UserRepository,
    private val stageUserRepository: StageUserRepository
) {

    @Transactional(readOnly = true)
    fun checkAccess(principalUserId: String, performanceId: Int) {
        if (userAccessService.isAdmin(principalUserId)) return

        val performance = performanceRepository.findById(performanceId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        val user = userRepository.findByUserId(principalUserId)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)
        val stageUser = stageUserRepository.findByUserId(user.id!!)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        if (stageUser.cityId != performance.cityEntity.id || stageUser.stage != performance.stageEntity.number) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }
}
