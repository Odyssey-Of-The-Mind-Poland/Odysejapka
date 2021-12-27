package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.Stage
import odyseja.odysejapka.port.StageUseCase
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/stage")
class StageController(
  private val stageUseCase: StageUseCase
) {

  @GetMapping("{cityId}")
  fun getStages(@PathVariable cityId: Int): List<Stage> {
    return stageUseCase.getStages(cityId)
  }

  @GetMapping
  fun getStages(): List<Stage> {
    return stageUseCase.getStages()
  }

  @Secured("ROLE_ADMIN")
  @PutMapping
  fun updateStage(@RequestBody stages: List<Stage>) {
    return stageUseCase.updateStage(stages)
  }
}