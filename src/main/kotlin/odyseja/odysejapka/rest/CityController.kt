package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.port.CityUseCase
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/city")
class CityController(
  private val cityUseCase: CityUseCase
) {

  @GetMapping()
  fun getCities(): MutableIterable<CityEntity?> {
    return cityUseCase.getCities()
  }

  @Secured("ROLE_ADMIN")
  @PostMapping
  fun saveCity(cityEntity: CityEntity) {
    cityUseCase.addCity(cityEntity)
  }

  @Secured("ROLE_ADMIN")
  @DeleteMapping("{cityId}")
  fun deleteCity(@PathVariable cityId: Int) {
    return cityUseCase.deleteCity(cityId)
  }
}