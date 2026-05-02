package odyseja.odysejapka.info

import odyseja.odysejapka.city.CityEntity
import org.springframework.data.repository.CrudRepository


interface InfoRepository : CrudRepository<InfoEntity?, Int?> {
  fun findByCity(city: CityEntity): Iterable<InfoEntity?>
  fun deleteByCityId(cityId: Int)
  fun deleteByCategoryId(categoryId: Int)
  fun findFirstById(id: Int): InfoEntity?
}