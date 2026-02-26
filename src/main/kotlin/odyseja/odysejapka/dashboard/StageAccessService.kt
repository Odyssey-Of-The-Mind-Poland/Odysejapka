package odyseja.odysejapka.dashboard

import odyseja.odysejapka.stage.StageUserRepository
import odyseja.odysejapka.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StageAccessService(
    private val userRepository: UserRepository,
    private val stageUserRepository: StageUserRepository
) {

    @Transactional(readOnly = true)
    fun getAccessibleStages(principalUserId: String, cityId: Int): List<Int> {
        val user = userRepository.findByUserId(principalUserId) ?: return emptyList()
        val stageUser = stageUserRepository.findByUserId(user.id!!) ?: return emptyList()
        if (stageUser.cityId != cityId) return emptyList()
        return listOf(stageUser.stage)
    }
}
