package odyseja.odysejapka.info

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(value = ["/info", "/api/info"])
class InfoController(
  private val infoService: InfoService
) {

  @GetMapping()
  @ResponseBody
  fun getInfo(@RequestParam(required = false) cityId: Int?): Iterable<Info?>? {
    return infoService.getInfo(cityId)
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

  @GetMapping("/map")
  @ResponseBody
  fun getInfoAndCategories(@RequestParam(required = false) cityId: Int?): Map<String, Any> {
    val infos = infoService.getInfo(cityId)
    val categories = infoService.getInfoCategory()
    return mapOf(
      "infos" to (infos?.toList() ?: emptyList()),
      "categories" to categories.map { it.toInfoCategory() }
    )
  }

  @Secured("ROLE_ADMINISTRATOR", "ROLE_LAPPKA")
  @PostMapping
  @ResponseBody
  fun addInfo(@RequestBody info: Info): Info {
    return infoService.addInfo(info)
  }

  @Secured("ROLE_ADMINISTRATOR", "ROLE_LAPPKA")
  @PutMapping
  fun updateInfo(@RequestBody info: Info): Info {
    return infoService.updateInfo(info)
  }

  @Secured("ROLE_ADMINISTRATOR", "ROLE_LAPPKA")
  @DeleteMapping("/{id}")
  @ResponseBody
  fun deleteInfo(@PathVariable id: Int) {
    infoService.deleteInfo(id)
  }

  @Secured("ROLE_ADMINISTRATOR", "ROLE_LAPPKA")
  @DeleteMapping("/category/{id}")
  @ResponseBody
  fun deleteInfoCategory(@PathVariable id: Int) {
    infoService.deleteInfoCategory(id)
  }
}