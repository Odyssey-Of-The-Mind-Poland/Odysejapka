package odyseja.odysejapka.change

import org.springframework.data.repository.CrudRepository

interface ChangeRepository : CrudRepository<ChangeEntity, Int> {

  fun findFirstByOrderByChangedAtDesc() : ChangeEntity
}