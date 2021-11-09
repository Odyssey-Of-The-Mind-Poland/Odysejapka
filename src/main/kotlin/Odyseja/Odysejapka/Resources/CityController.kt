package Odyseja.Odysejapka.Resources

import Odyseja.Odysejapka.data.model.City
import Odyseja.Odysejapka.repository.CityRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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