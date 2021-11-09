package Odyseja.Odysejapka.Resources

import Odyseja.Odysejapka.data.model.Age
import Odyseja.Odysejapka.repository.AgeRepository
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController()
@RequestMapping("/age")
class AgeController(
        private val ageRepository: AgeRepository
) {

    @GetMapping
    fun getAge(): MutableIterable<Age?> {
        return ageRepository.findAll()
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updateAge(@RequestBody ages: List<Age>){
        for (age in ages){
            val toEdit: Age = ageRepository.findById(age.id).get()
            toEdit.name = age.name
            ageRepository.save(toEdit)
        }
    }

}