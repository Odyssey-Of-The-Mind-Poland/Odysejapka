package Odyseja.Odysejapka.repository

import Odyseja.Odysejapka.data.model.City
import Odyseja.Odysejapka.data.model.Stage
import org.springframework.data.repository.CrudRepository
import java.util.*


interface StageRepository : CrudRepository<Stage, Int> {
    fun findFirstById(id: Int?): Stage?
    fun findFirstByNumberAndCity(number: Int?, city: City?): Stage?
    fun findAllByCity(id: Optional<City?>): MutableIterable<Stage?>
}