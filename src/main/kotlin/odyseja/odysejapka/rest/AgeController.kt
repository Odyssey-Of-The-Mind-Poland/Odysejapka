package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.AgeEntity
import odyseja.odysejapka.service.AgeService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/age")
class AgeController(private val ageService: AgeService) {

  @GetMapping
  fun getAge(): MutableIterable<AgeEntity?> {
    return ageService.getAge()
  }

  @Secured("ROLE_ADMIN")
  @PutMapping
  fun updateAge(@RequestBody ageEntities: List<AgeEntity>) {
    return ageService.updateAge(ageEntities)
  }

  @Secured("ROLE_ADMIN")
  @DeleteMapping("{ageId}")
  fun deleteAge(@PathVariable ageId: Int) {
    return ageService.deleteAge(ageId)
  }
}