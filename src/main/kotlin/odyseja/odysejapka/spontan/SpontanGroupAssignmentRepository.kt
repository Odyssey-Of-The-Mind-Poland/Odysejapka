package odyseja.odysejapka.spontan

import org.springframework.data.repository.CrudRepository

interface SpontanGroupAssignmentRepository : CrudRepository<SpontanGroupAssignmentEntity, Long> {
    fun findByCityId(cityId: Int): List<SpontanGroupAssignmentEntity>
    fun findByCityIdAndProblemAndAgeAndLeague(cityId: Int, problem: Int, age: Int, league: String): SpontanGroupAssignmentEntity?
}
