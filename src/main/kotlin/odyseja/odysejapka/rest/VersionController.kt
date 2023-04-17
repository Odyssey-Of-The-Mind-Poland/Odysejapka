package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.Version
import odyseja.odysejapka.service.ChangeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/version")
class VersionController(val changeService: ChangeService) {

  @GetMapping()
  fun getVersion(): Version {
    return changeService.getVersion()
  }
}