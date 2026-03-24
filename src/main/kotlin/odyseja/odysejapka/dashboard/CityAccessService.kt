package odyseja.odysejapka.dashboard

import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.spontan.SpontanUserService
import odyseja.odysejapka.stage.StageUserService
import odyseja.odysejapka.users.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityAccessService(
    private val cityService: CityService,
    private val userService: UserService,
    private val userAccessService: UserAccessService,
    private val stageUserService: StageUserService,
    private val spontanUserService: SpontanUserService
) {

    @Transactional(readOnly = true)
    fun getAccessibleCities(principalUserId: String): List<CityEntity> {
        if (userAccessService.isAdmin() || userAccessService.isKapitan()) {
            return cityService.getCities().filterNotNull()
        }

        val user = userService.getUserEntityOrNullByUserId(principalUserId) ?: return emptyList()

        val stageUser = stageUserService.getStageUserOrNullByUserId(user.id!!)
        if (stageUser != null) {
            return listOfNotNull(cityService.getCity(stageUser.cityId))
        }

        val spontanUser = spontanUserService.getSpontanUserOrNullByUserId(user.id!!)
        if (spontanUser != null) {
            return listOfNotNull(cityService.getCity(spontanUser.cityId))
        }

        return emptyList()
    }

}
