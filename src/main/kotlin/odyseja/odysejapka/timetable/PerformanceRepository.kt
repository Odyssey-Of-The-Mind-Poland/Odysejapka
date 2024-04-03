package odyseja.odysejapka.timetable

import odyseja.odysejapka.city.CityEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional


interface PerformanceRepository : CrudRepository<PerformanceEntity?, Int?> {
    @Transactional
    fun deleteByCityEntity(cityEntity: CityEntity?)

    fun findAllByCityEntity_Id(cityId: Int): List<PerformanceEntity>
}