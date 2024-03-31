package odyseja.odysejapka.breaking.change

import org.springframework.data.repository.CrudRepository

interface BreakingChangeRepository : CrudRepository<BreakingChangeEntity, Int> {

    fun findFirstByOrderByIdDesc(): BreakingChangeEntity?
}