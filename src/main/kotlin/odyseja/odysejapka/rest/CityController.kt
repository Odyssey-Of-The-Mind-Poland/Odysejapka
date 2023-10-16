package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.service.CityService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/city")
class CityController(
  private val cityService: CityService
) {

  @GetMapping()
  fun getCities(): MutableIterable<CityEntity?> {
    return cityService.getCities()
  }

  @Secured("ROLE_ADMIN")
  @PostMapping
  fun saveCity(@RequestBody cityEntity: CityEntity) {
    cityService.addCity(cityEntity)
  }

  @Secured("ROLE_ADMIN")
  @DeleteMapping("{cityId}")
  fun deleteCity(@PathVariable cityId: Int) {
    return cityService.deleteCity(cityId)
  }
}