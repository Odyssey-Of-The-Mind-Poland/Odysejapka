package odyseja.odysejapka.port

import odyseja.odysejapka.domain.Info
import odyseja.odysejapka.domain.InfoCategoryEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

interface InfoUseCase {

  fun getInfo(@PathVariable city: Int): Iterable<Info?>?

  fun getAllInfo(): List<Info>

  fun getInfoCategory(): MutableIterable<InfoCategoryEntity>

  fun addInfo(@RequestBody info: Info): Info

  fun updateInfo(@RequestBody info: Info): Info
}