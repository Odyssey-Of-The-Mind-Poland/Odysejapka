package odyseja.odysejapka.form.problem.form

import org.springframework.data.repository.CrudRepository

interface ProblemFormRepository : CrudRepository<ProblemFormEntity, Long> {
    fun findByProblem(problem: Int): ProblemFormEntity?
}

