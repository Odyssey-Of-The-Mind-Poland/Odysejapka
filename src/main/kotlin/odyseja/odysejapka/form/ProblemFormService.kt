package odyseja.odysejapka.form

import jakarta.transaction.Transactional
import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service

@Service
class ProblemFormService(
    private val problemFormRepository: ProblemFormRepository,
    private val changeService: ChangeService,
) {
    fun findByProblem(problem: Int): ProblemFormEntity {
        return problemFormRepository.findByProblem(problem)
        ?: ProblemFormEntity().apply { this.problem = problem }
    }

    @Transactional
    fun saveProblemForm(formProblem: ProblemFormEntity) {
        problemFormRepository.save(formProblem)
        changeService.updateVersion()
    }
}