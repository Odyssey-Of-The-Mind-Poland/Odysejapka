package odyseja.odysejapka.age

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/age")
class AgeController(private val ageService: AgeService) {

  @GetMapping
  fun getAge(principal: Principal): MutableIterable<AgeEntity?> {
    return ageService.getAge()
  }

  @Secured("ROLE_ADMINISTRATOR")
  @PutMapping
  fun updateAge(@RequestBody ageEntities: List<AgeEntity>) {
    return ageService.updateAge(ageEntities)
  }

  @Secured("ROLE_ADMINISTRATOR")
  @DeleteMapping("{ageId}")
  fun deleteAge(@PathVariable ageId: Int) {
    return ageService.deleteAge(ageId)
  }
}