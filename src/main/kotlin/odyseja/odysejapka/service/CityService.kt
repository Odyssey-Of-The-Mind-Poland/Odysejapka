package odyseja.odysejapka.service

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.port.CityUseCase
import org.springframework.stereotype.Service

@Service
internal class CityService(private val cityRepository: CityRepository) : CityUseCase {

  override fun getCities(): MutableIterable<CityEntity?> {
    return cityRepository.findAll()
  }
}