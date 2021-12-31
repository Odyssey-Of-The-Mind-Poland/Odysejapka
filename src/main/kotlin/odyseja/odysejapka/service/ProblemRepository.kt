package odyseja.odysejapka.service

import odyseja.odysejapka.domain.ProblemEntity
import org.springframework.data.repository.CrudRepository

interface ProblemRepository : CrudRepository<ProblemEntity, Int> {
    fun findFirstById(id: Int): ProblemEntity?
}