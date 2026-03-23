package odyseja.odysejapka.problem

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
    private val changeService: ChangeService
) {

    fun getProblems(): List<ProblemEntity?> {
        return problemRepository.findAll().sortedBy { it.id }
    }

    fun getProblem(problemId: Int): ProblemEntity {
        return problemRepository.findFirstById(problemId)
            ?: throw EntityNotFoundException("Nie znaleziono problemu o ID $problemId")
    }

    fun updateProblems(problemEntities: List<ProblemEntity>) {
        for (problem in problemEntities) {
            val toEdit: ProblemEntity = problemRepository.findById(problem.id)
                .getOrElse { ProblemEntity(problem.id, problem.name) }
            toEdit.name = problem.name
            problemRepository.save(toEdit)
        }

        changeService.updateVersion()
    }

    fun deleteProblem(problemId: Int) {
        problemRepository.deleteById(problemId)
        changeService.updateVersion()
    }
}