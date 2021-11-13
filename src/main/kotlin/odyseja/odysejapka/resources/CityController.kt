package odyseja.odysejapka.resources

import odyseja.odysejapka.data.model.City
import odyseja.odysejapka.repository.CityRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/city")
class CityController(
        private val cityRepository: CityRepository
) {

    @GetMapping()
    fun getCity(): MutableIterable<City?> {
        return cityRepository.findAll()
    }

}