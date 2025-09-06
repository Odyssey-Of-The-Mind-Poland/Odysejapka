package odyseja.odysejapka.form

import org.springframework.data.repository.CrudRepository

interface FormProblemRepository : CrudRepository<FormProblemEntity, Long> {
    fun findByProblem(problem: Int): FormProblemEntity?
}