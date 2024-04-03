package odyseja.odysejapka.problem

import org.springframework.data.repository.CrudRepository

interface ProblemRepository : CrudRepository<ProblemEntity, Int> {
    fun findFirstById(id: Int): ProblemEntity?
}