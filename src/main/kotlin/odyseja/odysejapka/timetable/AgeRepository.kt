package odyseja.odysejapka.timetable

import org.springframework.data.repository.CrudRepository

interface AgeRepository : CrudRepository<AgeEntity, Int> {
    fun findFirstById(id: Int): AgeEntity?
}