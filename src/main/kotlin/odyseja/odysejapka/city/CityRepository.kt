package odyseja.odysejapka.city

import org.springframework.data.repository.CrudRepository

interface CityRepository : CrudRepository<CityEntity?, Int?> {

  fun findFirstByName(name: String): CityEntity?

  fun findFirstById(id: Int): CityEntity
}