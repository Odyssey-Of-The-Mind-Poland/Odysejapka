package odyseja.odysejapka.stage

import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping(value = ["/stage", "/api/stage"])
class StageController(
  private val stageService: StageService,
  private val stageUserService: StageUserService
) {

  @GetMapping()
  fun getStages(@RequestParam(required = false) cityId: Int?): List<Stage> {
    return cityId?.let { stageService.getStagesByCity(cityId) } ?: stageService.getStagesByCity(0)
  }

  @Secured("ROLE_ADMINISTRATOR", "ROLE_LAPPKA")
  @PutMapping
  fun updateStage(@RequestBody stages: List<Stage>) {
    return stageService.updateStage(stages)
  }

  @Secured("ROLE_ADMINISTRATOR")
  @DeleteMapping("/{cityId}")
  fun clearStagesByCity(@PathVariable cityId: Int) {
    stageService.clearStagesByCity(cityId)
  }

  @Secured("ROLE_ADMINISTRATOR", "ROLE_LAPPKA")
  @GetMapping("/{cityId}/{stage}/credentials")
  fun getStageCredentials(
    @PathVariable cityId: Int,
    @PathVariable stage: Int
  ): ResponseEntity<StageUserCredentials> {
    val credentials = stageUserService.getCredentials(cityId, stage)
      ?: return ResponseEntity.notFound().build()
    return ResponseEntity.ok(credentials)
  }
}