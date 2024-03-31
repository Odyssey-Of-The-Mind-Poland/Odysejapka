package odyseja.odysejapka.city

import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityService(
  private val cityRepository: CityRepository,
  private val changeService: ChangeService
) {

  fun getCities(): MutableIterable<CityEntity?> {
    return cityRepository.findAll()
  }

  fun addCity(city: CityEntity) {
    cityRepository.save(city)

    changeService.updateVersion()
  }

  @Transactional
  fun deleteCity(cityId: Int) {
    cityRepository.deleteById(cityId)

    changeService.updateVersion()
  }
}