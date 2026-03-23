package odyseja.odysejapka.spontan

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.city.CityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpontanGroupAssignmentService(
    private val spontanGroupAssignmentRepository: SpontanGroupAssignmentRepository,
    private val cityService: CityService
) {
    fun getAssignmentEntity(cityId: Int, problem: Int, age: Int, league: String): SpontanGroupAssignmentEntity {
        return spontanGroupAssignmentRepository.findByCityIdAndProblemAndAgeAndLeague(cityId, problem, age, league)
            ?: throw EntityNotFoundException("Problem $problem, Grupa $age, Liga $league nie ma przypisanego spontana")
    }

    fun getAssignmentEntityById(assignmentId: Long): SpontanGroupAssignmentEntity {
        return spontanGroupAssignmentRepository.findFirstById(assignmentId)
            ?: throw EntityNotFoundException("Nie znaleziono przypisania spontana o ID $assignmentId")
    }

    fun getAssignmentEntitiesByCity(cityId: Int): List<SpontanGroupAssignmentEntity> {
        return spontanGroupAssignmentRepository.findByCityId(cityId)
    }

    fun getAssignmentEntitiesByUser(cityId: Int, spontanUserId: Long): List<SpontanGroupAssignmentEntity> {
        return spontanGroupAssignmentRepository.findByCityIdAndSpontanUserId(cityId, spontanUserId)
    }

    fun getAssignmentEntitiesByDefinition(spontanDefinitionId: Long): List<SpontanGroupAssignmentEntity> {
        return spontanGroupAssignmentRepository.findBySpontanDefinitionId(spontanDefinitionId)
    }

    @Transactional
    fun deleteAssignmentEntitiesByCity(cityId: Int) {
        spontanGroupAssignmentRepository.deleteByCityId(cityId)
    }

    @Transactional
    fun addAssignmentEntity(entity: SpontanGroupAssignmentEntity) {
        spontanGroupAssignmentRepository.save(entity)
    }

    fun getOrCreateAssignment(cityId: Int, groupId: GroupId): SpontanGroupAssignmentEntity {
        return spontanGroupAssignmentRepository.findByCityIdAndProblemAndAgeAndLeague(
            cityId, groupId.problem, groupId.age, groupId.league
        )
            ?: SpontanGroupAssignmentEntity().apply {
                this.city = cityService.getCity(cityId)
                this.problem = groupId.problem
                this.age = groupId.age
                this.league = groupId.league
            }
    }
}