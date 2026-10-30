package odyseja.odysejapka.form

import jakarta.transaction.Transactional
import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service

@Service
class ProblemFormService(
    private val problemFormRepository: ProblemFormRepository,
    private val changeService: ChangeService,
) {
    @Transactional
    fun findByProblem(problem: Int): ProblemFormEntity {
        val problemForm = problemFormRepository.findByProblem(problem)
            ?: ProblemFormEntity().also {
                it.problem = problem
                problemFormRepository.save(it)
            }

        return problemForm
    }

    @Transactional
    fun saveProblemForm(formProblem: ProblemFormEntity) {
        problemFormRepository.save(formProblem)
        changeService.updateVersion()
    }
}