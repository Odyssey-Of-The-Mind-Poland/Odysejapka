package odyseja.odysejapka.dashboard

import odyseja.odysejapka.stage.StageUserService
import odyseja.odysejapka.users.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StageAccessService(
    private val userService: UserService,
    private val stageUserService: StageUserService
) {

    @Transactional(readOnly = true)
    fun getAccessibleStages(principalUserId: String, cityId: Int): List<Int> {
        val user = userService.getUserEntityOrNullByUserId(principalUserId) ?: return emptyList()
        val stageUser = stageUserService.getStageUserOrNullByUserId(user.id!!) ?: return emptyList()
        if (stageUser.cityId != cityId) return emptyList()
        return listOf(stageUser.stage)
    }
}
