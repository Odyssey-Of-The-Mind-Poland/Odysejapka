package odyseja.odysejapka.port

import odyseja.odysejapka.domain.ProblemEntity
import org.springframework.web.bind.annotation.RequestBody

interface ProblemUseCase {

  fun getProblems(): MutableIterable<ProblemEntity?>

  fun updateProblem(@RequestBody problemEntities: List<ProblemEntity>)
}