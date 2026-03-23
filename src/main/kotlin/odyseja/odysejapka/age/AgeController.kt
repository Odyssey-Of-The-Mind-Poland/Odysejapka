package odyseja.odysejapka.age

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/age")
class AgeController(private val ageService: AgeService) {

  @GetMapping
  fun getAges(): MutableIterable<AgeEntity?> {
    return ageService.getAges()
  }

  @Secured("ROLE_ADMINISTRATOR")
  @PutMapping
  fun updateAge(@RequestBody ageEntities: List<AgeEntity>) {
    return ageService.updateAges(ageEntities)
  }

  @Secured("ROLE_ADMINISTRATOR")
  @DeleteMapping("{ageId}")
  fun deleteAge(@PathVariable ageId: Int) {
    return ageService.deleteAge(ageId)
  }
}