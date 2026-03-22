package odyseja.odysejapka.city

import org.springframework.data.repository.CrudRepository

interface CityRepository : CrudRepository<CityEntity?, Int?> {

  fun findFirstByName(name: String): CityEntity?
  fun findFirstById(cityId: Int): CityEntity?
  fun deleteFirstById(cityId: Int): CityEntity
}