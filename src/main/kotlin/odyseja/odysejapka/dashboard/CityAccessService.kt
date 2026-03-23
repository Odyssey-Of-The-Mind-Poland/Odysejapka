package odyseja.odysejapka.dashboard

import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.spontan.SpontanUserService
import odyseja.odysejapka.stage.StageUserRepository
import odyseja.odysejapka.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityAccessService(
    private val cityService: CityService,
    private val userRepository: UserRepository,
    private val userAccessService: UserAccessService,
    private val stageUserRepository: StageUserRepository,
    private val spontanUserService: SpontanUserService
) {

    @Transactional(readOnly = true)
    fun getAccessibleCities(principalUserId: String): List<CityEntity> {
        if (userAccessService.isAdmin() || userAccessService.isKapitan()) {
            return cityService.getCities().filterNotNull()
        }

        val user = userRepository.findByUserId(principalUserId) ?: return emptyList()

        val stageUser = stageUserRepository.findByUserId(user.id!!)
        if (stageUser != null) {
            return listOfNotNull(cityService.getCity(stageUser.cityId))
        }

        val spontanUser = spontanUserService.getSpontanUserByUserIdOrNull(user.id!!)
        if (spontanUser != null) {
            return listOfNotNull(cityService.getCity(spontanUser.cityId))
        }

        return emptyList()
    }

}
