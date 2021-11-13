package odyseja.odysejapka.resources

import odyseja.odysejapka.data.model.Age
import odyseja.odysejapka.repository.AgeRepository
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

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