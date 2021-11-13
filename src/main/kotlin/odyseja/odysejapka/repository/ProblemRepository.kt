package odyseja.odysejapka.repository

import odyseja.odysejapka.data.model.Age
import odyseja.odysejapka.data.model.Problem
import org.springframework.data.repository.CrudRepository

interface ProblemRepository : CrudRepository<Problem, Int> {
    fun findFirstById(id: Int): Problem
}