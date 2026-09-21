package odyseja.odysejapka.form.judge.team

import odyseja.odysejapka.city.CityEntity
import org.springframework.data.repository.CrudRepository

interface JudgeTeamRepository: CrudRepository<JudgeTeamEntity, Long> {
    fun findByProblemAndCity(problem: Int, city: CityEntity): JudgeTeamEntity?
    fun findByProblem(problem: Int): List<JudgeTeamEntity?>
    fun deleteByCity(city: CityEntity)
}