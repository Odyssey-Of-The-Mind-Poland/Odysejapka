package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.Performance
import odyseja.odysejapka.domain.Version
import odyseja.odysejapka.port.ChangeUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/version")
class VersionController(val changeUseCase: ChangeUseCase) {

  @GetMapping()
  fun getVersion(): Version {
    return changeUseCase.getVersion()
  }
}