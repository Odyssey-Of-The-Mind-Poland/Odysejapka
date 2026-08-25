package odyseja.odysejapka.form

import jakarta.transaction.Transactional
import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service

@Service
class FormProblemService(
    private val formProblemRepository: FormProblemRepository,
    private val changeService: ChangeService,
) {
    fun findByProblem(problem: Int): FormProblemEntity {
        return formProblemRepository.findByProblem(problem)
        ?: FormProblemEntity().apply { this.problem = problem }
    }

    @Transactional
    fun saveFormProblem(formProblem: FormProblemEntity) {
        formProblemRepository.save(formProblem)
        changeService.updateVersion()
    }
}