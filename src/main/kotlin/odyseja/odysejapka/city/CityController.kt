package odyseja.odysejapka.city

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/city", "/api/v1/city"])
class CityController(
    private val cityService: CityService
) {

    @GetMapping()
    fun getCities(): MutableIterable<CityEntity?> {
        return cityService.getCities()
    }

    @GetMapping("/{cityId}")
    fun getCity(@PathVariable cityId: Int): CityEntity {
        return cityService.getCity(cityId)
    }

    @GetMapping("/name/{cityName}")
    fun getCityByName(@PathVariable cityName: String): CityEntity {
        return cityService.getCityByName(cityName)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping
    fun saveCity(@RequestBody cityRequest: CreateCityRequest): CityEntity {
        return cityService.addCity(cityRequest)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping("/{cityId}")
    fun deleteCity(@PathVariable cityId: Int) {
        cityService.deleteCity(cityId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping
    fun clearCities() {
        cityService.clearCities()
    }
}