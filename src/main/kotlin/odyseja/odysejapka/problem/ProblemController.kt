package odyseja.odysejapka.problem

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/problem")
class ProblemController(private val problemService: ProblemService) {

    @GetMapping
    fun getProblems(): List<ProblemEntity?> {
        return problemService.getProblems()
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updateProblem(@RequestBody problemEntities: List<ProblemEntity>) {
        return problemService.updateProblem(problemEntities)
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("{problemId}")
    fun deleteProblem(@PathVariable problemId: Int) {
        return problemService.deleteProblem(problemId)
    }
}