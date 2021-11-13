package Odyseja.Odysejapka

import odyseja.odysejapka.data.model.Problem
import odyseja.odysejapka.repository.ProblemRepository
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/problem")
class ProblemController(
        private val problemRepository: ProblemRepository
) {

    @GetMapping
    fun getProblems(): MutableIterable<Problem?> {
        return problemRepository.findAll()
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updateProblem(@RequestBody problems: List<Problem>){
        for (problem in problems){
            val toEdit: Problem = problemRepository.findById(problem.id).get()
            toEdit.name = problem.name
            problemRepository.save(toEdit)
        }
    }
}