package odyseja.odysejapka.info

import org.springframework.data.repository.CrudRepository

interface InfoCategoryRepository : CrudRepository<InfoCategoryEntity, Int> {
  fun findFirstById(id: Int): InfoCategoryEntity
}