package odyseja.odysejapka.spontan

import org.springframework.data.repository.CrudRepository

interface SpontanGroupAssignmentRepository : CrudRepository<SpontanGroupAssignmentEntity, Long> {
    fun findFirstById(assignmentId: Long): SpontanGroupAssignmentEntity?
    fun findByCityId(cityId: Int): List<SpontanGroupAssignmentEntity>
    fun findByCityIdAndProblemAndAgeAndLeague(cityId: Int, problem: Int, age: Int, league: String): SpontanGroupAssignmentEntity?
    fun findByCityIdAndSpontanUserId(cityId: Int, spontanUserId: Long): List<SpontanGroupAssignmentEntity>
    fun findBySpontanDefinitionId(spontanDefinitionId: Long): List<SpontanGroupAssignmentEntity>
    fun deleteByCityId(cityId: Int)
}
