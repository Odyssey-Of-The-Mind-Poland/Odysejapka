package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.Info
import odyseja.odysejapka.domain.InfoCategoryEntity
import odyseja.odysejapka.service.InfoService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/info")
class InfoController(
  private val infoService: InfoService
) {

  @GetMapping("/{city}")
  @ResponseBody
  fun getInfo(@PathVariable city: Int): Iterable<Info?>? {
    return infoService.getInfo(city)
  }

  @GetMapping("/id/{info}")
  @ResponseBody
  fun getInfoById(@PathVariable info: Int): Info {
    return infoService.getInfoById(info)
  }

  @GetMapping("/category")
  @ResponseBody
  fun getInfoCategory(): Iterable<InfoCategoryEntity> {
    return infoService.getInfoCategory()
  }

  @GetMapping()
  fun getAllInfo(): List<Info> {
    return infoService.getAllInfo()
  }

  @Secured("ROLE_ADMIN")
  @PostMapping
  fun addInfo(@RequestBody info: Info): Info {
    return infoService.addInfo(info)
  }

  @Secured("ROLE_ADMIN")
  @PutMapping
  fun updateInfo(@RequestBody info: Info): Info {
    return infoService.updateInfo(info)
  }

  @Secured("ROLE_ADMIN")
  @DeleteMapping("/{id}")
  @ResponseBody
  fun deleteInfo(@PathVariable id: Int) {
    infoService.deleteInfo(id)
  }
}