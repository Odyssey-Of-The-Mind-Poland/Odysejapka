package odyseja.odysejapka.service

import odyseja.odysejapka.domain.CityEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CityRepository : CrudRepository<CityEntity?, Int?> {

  fun findFirstByName(name: String): CityEntity?

  fun findFirstById(id: Int): CityEntity
}