package odyseja.odysejapka.port

import odyseja.odysejapka.domain.InfoEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

interface InfoUseCase {

  fun getInfo(@PathVariable city: String): Iterable<InfoEntity?>?

  fun getAllInfo(): MutableIterable<InfoEntity?>

  fun addInfo(@RequestBody infoEntity: List<InfoEntity>) : List<InfoEntity>
}