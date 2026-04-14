package odyseja.odysejapka.problem

import jakarta.transaction.Transactional
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

    @Transactional
    fun saveProblem(problem: ProblemEntity): ProblemEntity {
        return problemRepository.save(problem).also {
            changeService.updateVersion()
        }
    }

    @Transactional
    fun getProblem(problemId: Int): ProblemEntity {
        return problemRepository.findFirstById(problemId)
            ?: saveProblem(ProblemEntity(problemId, "Problem $problemId"))
    }

    @Transactional
    fun updateProblems(problemEntities: List<ProblemEntity>) {
        for (problem in problemEntities) {
            val toEdit: ProblemEntity = problemRepository.findById(problem.id)
                .getOrElse { ProblemEntity(problem.id, problem.name) }
            toEdit.apply{
                name = problem.name
                validate()
            }
            problemRepository.save(toEdit)
        }
        changeService.updateVersion()
    }

    @Transactional
    fun deleteProblem(problemId: Int) {
        problemRepository.deleteById(problemId)
        changeService.updateVersion()
    }
}