package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.ProblemEntity
import odyseja.odysejapka.port.ProblemUseCase
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/problem")
class ProblemController(
        private val problemUseCase: ProblemUseCase
) {

    @GetMapping
    fun getProblems(): MutableIterable<ProblemEntity?> {
        return problemUseCase.getProblems()
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updateProblem(@RequestBody problemEntities: List<ProblemEntity>) {
        return problemUseCase.updateProblem(problemEntities)
    }
}