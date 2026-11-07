package odyseja.odysejapka.problem

import jakarta.validation.Valid
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/problem", "/api/problem"])
class ProblemController(private val problemService: ProblemService) {

    @GetMapping
    fun getProblems(): List<ProblemEntity?> {
        return problemService.getProblemsWithNewLines()
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PutMapping
    fun updateProblems(@Valid @RequestBody problemEntities: List<ProblemEntity>) {
        return problemService.updateProblems(problemEntities)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping("/{problemId}")
    fun deleteProblem(@PathVariable problemId: Int) {
        return problemService.deleteProblem(problemId)
    }
}