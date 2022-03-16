package odyseja.odysejapka.service

import odyseja.odysejapka.domain.ChangeEntity
import org.springframework.data.repository.CrudRepository

interface ChangeRepository : CrudRepository<ChangeEntity, Int> {

  fun findFirstByOrderByChangedAtDesc() : ChangeEntity
}