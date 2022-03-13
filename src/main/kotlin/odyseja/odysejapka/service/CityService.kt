package odyseja.odysejapka.service

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.port.CityUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class CityService(private val cityRepository: CityRepository) : CityUseCase {

  override fun getCities(): MutableIterable<CityEntity?> {
    return cityRepository.findAll()
  }

  override fun addCity(city: CityEntity) {
    cityRepository.save(city)
  }

  @Transactional
  override fun deleteCity(cityId: Int) {
    cityRepository.deleteById(cityId)
  }
}