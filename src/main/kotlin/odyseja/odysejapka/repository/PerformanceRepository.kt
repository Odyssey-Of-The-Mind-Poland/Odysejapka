package odyseja.odysejapka.repository

import odyseja.odysejapka.data.model.City
import odyseja.odysejapka.data.model.Performance
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface PerformanceRepository : CrudRepository<Performance?, Int?> {
    fun deleteByCity(city: City?)
}