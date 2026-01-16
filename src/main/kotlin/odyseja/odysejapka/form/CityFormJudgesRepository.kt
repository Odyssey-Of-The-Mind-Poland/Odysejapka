package odyseja.odysejapka.form

import odyseja.odysejapka.city.CityEntity
import org.springframework.data.repository.CrudRepository

interface CityFormJudgesRepository : CrudRepository<CityFormJudgesEntity, Long> {
    fun findByProblemAndCity(problem: Int, city: CityEntity): CityFormJudgesEntity?
    fun findByProblem(problem: Int): List<CityFormJudgesEntity>
}

