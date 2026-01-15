package odyseja.odysejapka.form

import org.springframework.data.repository.CrudRepository

interface FormEntryRepository : CrudRepository<FormEntryEntity, Long> {

    fun findByProblem(problem: Int) : List<FormEntryEntity>
}