package odyseja.odysejapka.service

import odyseja.odysejapka.domain.InfoCategoryEntity
import org.springframework.data.repository.CrudRepository

interface InfoCategoryRepository : CrudRepository<InfoCategoryEntity, Int> {
  fun findFirstById(id: Int): InfoCategoryEntity
}