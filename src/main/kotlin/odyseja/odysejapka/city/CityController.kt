package odyseja.odysejapka.city

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/city")
class CityController(
  private val cityService: CityService?
) {

  @GetMapping()
  fun getCities(): MutableIterable<CityEntity?> {
    return cityService!!.getCities()
  }

  @Secured("ROLE_ADMINISTRATOR")
  @PostMapping
  fun saveCity(@RequestBody cityRequest: CreateCityRequest): CityEntity {
    return cityService!!.addCity(cityRequest)
  }

  @Secured("ROLE_ADMINISTRATOR")
  @DeleteMapping("{cityId}")
  fun deleteCity(@PathVariable cityId: Int) {
    return cityService!!.deleteCity(cityId)
  }
}