package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.AgeEntity
import odyseja.odysejapka.port.UserUseCase
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/age")
class AgeController(private val userUseCase: UserUseCase) {

    @GetMapping
    fun getAge(): MutableIterable<AgeEntity?> {
        return userUseCase.getAge()
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updateAge(@RequestBody ageEntities: List<AgeEntity>) {
        return userUseCase.updateAge(ageEntities)
    }
}