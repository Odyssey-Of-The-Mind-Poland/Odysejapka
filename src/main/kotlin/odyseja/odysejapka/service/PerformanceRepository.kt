package odyseja.odysejapka.service

import odyseja.odysejapka.domain.CityEntity
import odyseja.odysejapka.domain.PerformanceEntity
import org.springframework.data.repository.CrudRepository


interface PerformanceRepository : CrudRepository<PerformanceEntity?, Int?> {
    fun deleteByCityEntity(cityEntity: CityEntity?)
}