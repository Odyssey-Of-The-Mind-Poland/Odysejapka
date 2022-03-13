package odyseja.odysejapka.service

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.domain.InfoEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface InfoRepository : CrudRepository<InfoEntity?, Int?> {
    fun findByCity(city: CityEntity): Iterable<InfoEntity>
}