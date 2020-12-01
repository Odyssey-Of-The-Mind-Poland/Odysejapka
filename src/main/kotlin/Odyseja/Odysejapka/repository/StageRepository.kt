package Odyseja.Odysejapka.repository

import Odyseja.Odysejapka.data.model.Stage
import org.springframework.data.repository.CrudRepository


interface StageRepository : CrudRepository<Stage?, Int?> {
    fun findFirstById(id: Int?): Stage?
}