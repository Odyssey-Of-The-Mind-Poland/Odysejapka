package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.Info
import odyseja.odysejapka.domain.InfoCategoryEntity
import odyseja.odysejapka.port.InfoUseCase
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/info")
class InfoController(
  private val infoUseCase: InfoUseCase
) {

  @GetMapping("/{city}")
  @ResponseBody
  fun getInfo(@PathVariable city: Int): Iterable<Info?>? {
    return infoUseCase.getInfo(city)
  }

  @GetMapping("/category")
  @ResponseBody
  fun getInfoCategory(): Iterable<InfoCategoryEntity> {
    return infoUseCase.getInfoCategory()
  }

  @GetMapping()
  fun getAllInfo(): List<Info> {
    return infoUseCase.getAllInfo()
  }

  @Secured("ROLE_ADMIN")
  @PostMapping
  fun addInfo(@RequestBody info: Info): Info {
    return infoUseCase.addInfo(info)
  }

  @Secured("ROLE_ADMIN")
  @PutMapping
  fun updateInfo(@RequestBody info: Info): Info {
    return infoUseCase.updateInfo(info)
  }

  @DeleteMapping("/{id}")
  @ResponseBody
  fun deleteInfo(@PathVariable id: Int) {
    infoUseCase.deleteInfo(id)
  }
}