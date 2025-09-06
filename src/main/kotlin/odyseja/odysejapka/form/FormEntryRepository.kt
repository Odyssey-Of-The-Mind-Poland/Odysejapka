package odyseja.odysejapka.form

import org.springframework.data.repository.CrudRepository

interface FormEntryRepository : CrudRepository<FormEntryEntity, Long> {

    fun deleteAllByProblem(problem: Int)
    fun findByProblem(problem: Int) : List<FormEntryEntity>
}