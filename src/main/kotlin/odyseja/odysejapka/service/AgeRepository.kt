package odyseja.odysejapka.service

import odyseja.odysejapka.domain.AgeEntity
import org.springframework.data.repository.CrudRepository

interface AgeRepository : CrudRepository<AgeEntity, Int> {
    fun findFirstById(id: Int): AgeEntity
}