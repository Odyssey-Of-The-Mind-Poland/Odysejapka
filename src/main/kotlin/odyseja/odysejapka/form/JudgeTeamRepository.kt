package odyseja.odysejapka.form

import odyseja.odysejapka.city.CityEntity
import org.springframework.data.repository.CrudRepository

interface JudgeTeamRepository: CrudRepository<JudgeTeamEntity, Long> {
    fun findByProblemFormAndCity(problemForm: ProblemFormEntity, city: CityEntity): JudgeTeamEntity?
    fun deleteByCity(city: CityEntity)
}