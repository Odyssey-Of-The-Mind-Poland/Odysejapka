package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.port.CityUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/city")
class CityController(
  private val cityUseCase: CityUseCase
) {

  @GetMapping()
  fun getCities(): MutableIterable<CityEntity?> {
    return cityUseCase.getCities()
  }

  @PostMapping
  fun saveCity(cityEntity: CityEntity) {
    cityUseCase.addCity(cityEntity)
  }
}