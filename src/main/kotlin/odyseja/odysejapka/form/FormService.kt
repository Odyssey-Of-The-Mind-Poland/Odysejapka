package odyseja.odysejapka.form

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(
    private val formEntryRepository: FormEntryRepository,
    private val formProblemRepository: FormProblemRepository
) {

    @Transactional
    fun setFormEntries(problem: Int, formEntries: List<FormEntry>) {
        formEntryRepository.deleteAllByProblem(problem)
        formEntryRepository.saveAll(formEntries.map { FormEntryEntity.from(problem, it) })
    }

    fun getFormEntries(problem: Int): List<FormEntry> {
        return formEntryRepository.findByProblem(problem).map { it.toFormEntry() }
    }

    @Transactional
    fun setJudgeCount(problem: Int, count: Int): Int {
        val problem = formProblemRepository.findByProblem(problem) ?: FormProblemEntity().apply {
            this.problem = problem
        }
        problem.judgeCount = count
        formProblemRepository.save(problem)
        return count
    }

    fun getJudgeCount(problem: Int): Int {
        return formProblemRepository.findByProblem(problem)?.judgeCount ?: 0
    }
}