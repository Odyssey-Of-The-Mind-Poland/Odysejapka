package Odyseja.Odysejapka.repository

import Odyseja.Odysejapka.data.model.City
import Odyseja.Odysejapka.data.model.Performance
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface PerformanceRepository : CrudRepository<Performance?, Int?> {
    fun deleteByCity(city: City?)
}