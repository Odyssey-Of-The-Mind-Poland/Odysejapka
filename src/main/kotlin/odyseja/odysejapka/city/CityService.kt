package odyseja.odysejapka.city

import jakarta.persistence.EntityNotFoundException
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
    val saved = cityRepository.save(CityEntity(0, city.name, city.level))
    changeService.updateVersion()
    return saved
  }

  @Transactional
  fun updateCity(city: CityEntity): CityEntity {
    val updatedCity = getCity(city.id)
    updatedCity.apply {
      name = city.name
      level = city.level
    }
    return cityRepository.save(updatedCity)
  }

  fun getCityByName(cityName: String): CityEntity {
    return cityRepository.findFirstByName(cityName) ?: throw EntityNotFoundException("Nie znaleziono miasta o nazwie $cityName")
  }

  fun getCity(cityId: Int): CityEntity {
    return cityRepository.findFirstById(cityId) ?: throw EntityNotFoundException("Nie znaleziono miasta o ID $cityId")
  }
}