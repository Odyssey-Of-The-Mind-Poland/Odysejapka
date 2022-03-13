package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.AgeEntity
import odyseja.odysejapka.port.AgeUseCase
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/age")
class AgeController(private val ageUseCase: AgeUseCase) {

  @GetMapping
  fun getAge(): MutableIterable<AgeEntity?> {
    return ageUseCase.getAge()
  }

  @Secured("ROLE_ADMIN")
  @PutMapping
  fun updateAge(@RequestBody ageEntities: List<AgeEntity>) {
    return ageUseCase.updateAge(ageEntities)
  }

  @Secured("ROLE_ADMIN")
  @DeleteMapping("{ageId}")
  fun deleteAge(@PathVariable ageId: Int) {
    return ageUseCase.deleteAge(ageId)
  }
}