package odyseja.odysejapka.port

import odyseja.odysejapka.domain.Stage
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

interface StageUseCase {

  fun getStages(@PathVariable city: Int): List<Stage>

  fun getStages(): List<Stage>

  fun updateStage(@RequestBody stages: List<Stage>)
}