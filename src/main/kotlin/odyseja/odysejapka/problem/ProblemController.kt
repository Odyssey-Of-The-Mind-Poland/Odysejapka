package odyseja.odysejapka.problem

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/problem", "/api/problem"])
class ProblemController(private val problemService: ProblemService) {

    @GetMapping
    fun getProblems(): List<ProblemEntity?> {
        return problemService.getProblems().map { problem ->
            problem?.let { ProblemEntity(it.id, it.name.replace("\\n", "")) }
        }
    }

    @GetMapping("/v2")
    fun getProblemsV2(): List<ProblemEntity?> {
        return problemService.getProblems()
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PutMapping
    fun updateProblem(@RequestBody problemEntities: List<ProblemEntity>) {
        return problemService.updateProblem(problemEntities)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping("{problemId}")
    fun deleteProblem(@PathVariable problemId: Int) {
        return problemService.deleteProblem(problemId)
    }
}