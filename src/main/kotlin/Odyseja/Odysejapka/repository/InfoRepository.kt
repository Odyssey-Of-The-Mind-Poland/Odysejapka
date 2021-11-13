package Odyseja.Odysejapka.repository

import Odyseja.Odysejapka.data.model.Info
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface InfoRepository : CrudRepository<Info?, Int?> {
    fun findByCity(@Param("city") city: String): Iterable<Info>
    fun deleteByCity(city: String?)
}