package odyseja.odysejapka.spontan

import odyseja.odysejapka.city.CityRepository
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class SpontanService(
    private val spontanDefinitionRepository: SpontanDefinitionRepository,
    private val spontanGroupAssignmentRepository: SpontanGroupAssignmentRepository,
    private val cityRepository: CityRepository,
    private val performanceRepository: PerformanceRepository
) {

    fun getAll(): List<SpontanDefinition> {
        return spontanDefinitionRepository.findAll().map { it.toSpontanDefinition() }
    }

    fun getById(id: Long): SpontanDefinition {
        val entity = spontanDefinitionRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return entity.toSpontanDefinition()
    }

    @Transactional
    fun create(definition: SpontanDefinition): SpontanDefinition {
        val entity = SpontanDefinitionEntity().apply { applyFrom(definition) }
        return spontanDefinitionRepository.save(entity).toSpontanDefinition()
    }

    @Transactional
    fun update(id: Long, definition: SpontanDefinition): SpontanDefinition {
        val entity = spontanDefinitionRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        entity.applyFrom(definition)
        return spontanDefinitionRepository.save(entity).toSpontanDefinition()
    }

    @Transactional
    fun delete(id: Long) {
        if (!spontanDefinitionRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        spontanDefinitionRepository.deleteById(id)
    }

    fun getGroupAssignments(cityId: Int): List<SpontanGroupAssignment> {
        if (!cityRepository.existsById(cityId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")
        }

        val groupIds = performanceRepository.findAllByCityEntity_Id(cityId)
            .map { GroupId(it.problemEntity.id, it.ageEntity.id, it.league ?: "") }
            .distinct()
            .sortedWith(compareBy({ it.problem }, { it.age }, { it.league }))

        val assignments = spontanGroupAssignmentRepository.findByCityId(cityId)
            .associateBy { it.toGroupId() }

        return groupIds.map { groupId ->
            assignments[groupId]?.toSpontanGroupAssignment()
                ?: SpontanGroupAssignment(cityId = cityId, groupId = groupId)
        }
    }

    @Transactional
    fun assignSpontan(cityId: Int, groupId: GroupId, spontanDefinitionId: Long?): SpontanGroupAssignment {
        val spontanDefinition = spontanDefinitionId?.let {
            spontanDefinitionRepository.findById(it).orElse(null)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Spontan definition not found")
        }

        val assignment = getOrCreateAssignment(cityId, groupId)
        assignment.spontanDefinition = spontanDefinition
        return spontanGroupAssignmentRepository.save(assignment).toSpontanGroupAssignment()
    }

    @Transactional
    fun setJudgeCount(cityId: Int, groupId: GroupId, judgeCount: Int): SpontanGroupAssignment {
        val assignment = getOrCreateAssignment(cityId, groupId)
        assignment.judgeCount = judgeCount
        return spontanGroupAssignmentRepository.save(assignment).toSpontanGroupAssignment()
    }

    private fun getOrCreateAssignment(cityId: Int, groupId: GroupId): SpontanGroupAssignmentEntity {
        return spontanGroupAssignmentRepository
            .findByCityIdAndProblemAndAgeAndLeague(cityId, groupId.problem, groupId.age, groupId.league)
            ?: SpontanGroupAssignmentEntity().apply {
                this.city = cityRepository.findById(cityId).orElse(null)
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")
                this.problem = groupId.problem
                this.age = groupId.age
                this.league = groupId.league
            }
    }
}
