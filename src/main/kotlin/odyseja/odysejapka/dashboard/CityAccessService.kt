package odyseja.odysejapka.dashboard

import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityRepository
import odyseja.odysejapka.stage.StageUserRepository
import odyseja.odysejapka.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityAccessService(
    private val cityRepository: CityRepository,
    private val userRepository: UserRepository,
    private val userAccessService: UserAccessService,
    private val stageUserRepository: StageUserRepository
) {

    @Transactional(readOnly = true)
    fun getAccessibleCities(principalUserId: String): List<CityEntity> {
        if (userAccessService.isAdmin() || userAccessService.isKapitan()) {
            return cityRepository.findAll().filterNotNull()
        }

        val user = userRepository.findByUserId(principalUserId) ?: return emptyList()
        val stageUser = stageUserRepository.findByUserId(user.id!!) ?: return emptyList()
        return listOfNotNull(cityRepository.findById(stageUser.cityId).orElse(null))
    }

}
