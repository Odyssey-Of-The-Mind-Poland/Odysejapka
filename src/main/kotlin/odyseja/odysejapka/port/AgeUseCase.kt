package odyseja.odysejapka.port

import odyseja.odysejapka.domain.AgeEntity
import org.springframework.web.bind.annotation.RequestBody

interface AgeUseCase {

  fun getAge(): MutableIterable<AgeEntity?>

  fun updateAge(@RequestBody ageEntities: List<AgeEntity>)

  fun deleteAge(ageId: Int)
}