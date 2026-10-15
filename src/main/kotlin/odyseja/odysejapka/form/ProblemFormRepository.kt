package odyseja.odysejapka.form

import org.springframework.data.repository.CrudRepository

interface ProblemFormRepository : CrudRepository<ProblemFormEntity, Long> {
    fun findByProblem(problem: Int): ProblemFormEntity?
}

