package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.Stage
import odyseja.odysejapka.service.StageService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/stage")
class StageController(
  private val stageService: StageService
) {

  @GetMapping()
  fun getStages(@RequestParam(required = false) cityId: Int?): List<Stage> {
    return cityId?.let { stageService.getStages(cityId) } ?: stageService.getStages()
  }

  @Secured("ROLE_ADMIN")
  @PutMapping
  fun updateStage(@RequestBody stages: List<Stage>) {
    return stageService.updateStage(stages)
  }
}