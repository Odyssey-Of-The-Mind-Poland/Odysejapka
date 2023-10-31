package odyseja.odysejapka.service

import odyseja.odysejapka.domain.BreakingChangeEntity
import org.springframework.data.repository.CrudRepository

interface BreakingChangeRepository : CrudRepository<BreakingChangeEntity, Int> {

    fun findFirstByOrderByIdDesc(): BreakingChangeEntity?
}