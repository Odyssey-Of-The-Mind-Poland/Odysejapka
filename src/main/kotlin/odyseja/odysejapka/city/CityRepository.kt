package odyseja.odysejapka.city

import org.springframework.data.repository.CrudRepository

interface CityRepository : CrudRepository<CityEntity?, Int?> {

  fun findFirstById(cityId: Int): CityEntity?
  fun findFirstByLevel(level: KonkursLevel): CityEntity?
}