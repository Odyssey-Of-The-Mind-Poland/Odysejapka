package odyseja.odysejapka.service

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.domain.InfoEntity
import org.springframework.data.repository.CrudRepository


interface InfoRepository : CrudRepository<InfoEntity?, Int?> {
  fun findByCity(city: CityEntity): Iterable<InfoEntity>

  fun findAllByOrderBySortNumber(): Iterable<InfoEntity>
}