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

  @Transactional
  fun addCity(city: CreateCityRequest): CityEntity {
    val saved = cityRepository.save(CityEntity(0, city.name))
    changeService.updateVersion()
    return saved
  }

  @Transactional
  fun deleteCity(cityId: Int) {
    cityRepository.deleteById(cityId)

    changeService.updateVersion()
  }
}