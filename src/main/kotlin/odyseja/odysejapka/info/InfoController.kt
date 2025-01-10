package odyseja.odysejapka.info

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/info")
class InfoController(
  private val infoService: InfoService
) {

  @GetMapping()
  @ResponseBody
  fun getInfo(@RequestParam(required = false) cityId: Int?): Iterable<Info?>? {
    return cityId?.let { infoService.getInfo(cityId) } ?: infoService.getInfo(0)
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

  @Secured("ROLE_ADMIN")
  @PostMapping
  @ResponseBody
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