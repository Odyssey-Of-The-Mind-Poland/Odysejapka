package Odyseja.Odysejapka.repository

import Odyseja.Odysejapka.data.model.Age
import Odyseja.Odysejapka.data.model.Problem
import org.springframework.data.repository.CrudRepository

interface ProblemRepository : CrudRepository<Problem, Int> {
    fun findFirstById(id: Int): Problem
}