package odyseja.odysejapka.repository

import odyseja.odysejapka.data.model.Age
import org.springframework.data.repository.CrudRepository

interface AgeRepository : CrudRepository<Age, Int> {
    fun findFirstById(id: Int): Age
}