package odyseja.odysejapka.form

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(private val formEntryRepository: FormEntryRepository) {

    @Transactional
    fun setFormEntries(problem: Int, formEntries: List<FormEntry>) {
        formEntryRepository.deleteAllByProblem(problem)
        formEntryRepository.saveAll(formEntries.map { FormEntryEntity.from(problem, it) })
    }

    fun getFormEntries(problem: Int): List<FormEntry> {
        return formEntryRepository.findByProblem(problem).map { it.toFormEntry() }
    }
}