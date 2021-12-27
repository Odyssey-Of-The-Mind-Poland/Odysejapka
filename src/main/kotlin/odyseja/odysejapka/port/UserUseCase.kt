package odyseja.odysejapka.port

import odyseja.odysejapka.domain.AgeEntity
import org.springframework.web.bind.annotation.RequestBody

interface UserUseCase {

  fun getAge(): MutableIterable<AgeEntity?>

  fun updateAge(@RequestBody ageEntities: List<AgeEntity>)
}