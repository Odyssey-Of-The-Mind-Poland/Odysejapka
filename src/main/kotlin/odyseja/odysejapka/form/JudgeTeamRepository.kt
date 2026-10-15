package odyseja.odysejapka.form

import odyseja.odysejapka.city.CityEntity
import org.springframework.data.repository.CrudRepository

interface JudgeTeamRepository: CrudRepository<JudgeTeamEntity, Long> {
    fun findByProblemAndCity(problem: Int, city: CityEntity): JudgeTeamEntity?
    fun findByProblem(problem: Int): List<JudgeTeamEntity?>
    fun deleteByProblemAndCity(problem: Int, city: CityEntity)
    fun deleteByCity(city: CityEntity)
}