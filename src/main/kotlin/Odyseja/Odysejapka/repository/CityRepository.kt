package Odyseja.Odysejapka.repository

import Odyseja.Odysejapka.data.model.City
import org.springframework.data.repository.CrudRepository

interface CityRepository : CrudRepository<City?, Int?>{
    fun findFirstByName(name: String): City?
    fun findFirstById(id: Int): City
}