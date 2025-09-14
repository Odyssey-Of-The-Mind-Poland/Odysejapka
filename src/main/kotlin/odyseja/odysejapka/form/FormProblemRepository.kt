package odyseja.odysejapka.form

import odyseja.odysejapka.city.CityEntity
import org.springframework.data.repository.CrudRepository

interface FormProblemRepository : CrudRepository<FormProblemEntity, Long> {
    fun findByProblemAndCity(problem: Int, city: CityEntity): FormProblemEntity?
}