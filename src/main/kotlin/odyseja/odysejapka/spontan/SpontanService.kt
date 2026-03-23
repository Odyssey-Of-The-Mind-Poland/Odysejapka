package odyseja.odysejapka.spontan

import com.ezylang.evalex.Expression
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.timetable.TimeTableService
import odyseja.odysejapka.users.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class SpontanService(
    private val spontanDefinitionRepository: SpontanDefinitionRepository,
    private val cityService: CityService,
    private val timeTableService: TimeTableService,
    private val spontanUserRepository: SpontanUserRepository,
    private val userRepository: UserRepository,
    private val spontanAccessService: SpontanAccessService,
    private val spontanGroupAssignmentService: SpontanGroupAssignmentService
) {

    fun getAllSpontans(): List<SpontanDefinition> {
        return spontanDefinitionRepository.findAll().map { it.toSpontanDefinition() }
    }

    fun getSpontanById(id: Long): SpontanDefinition {
        val entity = spontanDefinitionRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return entity.toSpontanDefinition()
    }

    @Transactional
    fun createSpontan(definition: SpontanDefinition): SpontanDefinition {
        if (definition.type == SpontanType.MANUAL) {
            validateFields(definition.fields)
        }
        val entity = SpontanDefinitionEntity().apply { applyFrom(definition) }
        return spontanDefinitionRepository.save(entity).toSpontanDefinition()
    }

    @Transactional
    fun updateSpontan(id: Long, definition: SpontanDefinition): SpontanDefinition {
        val entity = spontanDefinitionRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (definition.type == SpontanType.MANUAL) {
            validateFields(definition.fields)
        }
        entity.applyFrom(definition)
        return spontanDefinitionRepository.save(entity).toSpontanDefinition()
    }

    @Transactional
    fun deleteSpontan(id: Long) {
        if (!spontanDefinitionRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        for (assignment in spontanGroupAssignmentService.getAssignmentEntitiesByDefinition(id)) {
            assignment.spontanDefinition = null
            spontanGroupAssignmentService.addAssignmentEntity(assignment)
        }
        spontanDefinitionRepository.deleteById(id)
    }

    fun getGroupAssignments(cityId: Int): List<SpontanGroupAssignment> {
        cityService.getCity(cityId)

        val accessibleIds = spontanAccessService.accessibleAssignmentIds(cityId)

        val groupIds = timeTableService.getPerformanceEntitiesByCity(cityId)
            .filter { !it.isExcludedFromScoring() }
            .map { GroupId(it.problemEntity.id, it.ageEntity.id, it.league ?: "") }
            .distinct()
            .sortedWith(compareBy({ it.problem }, { it.age }, { it.league }))

        val assignments = spontanGroupAssignmentService.getAssignmentEntitiesByCity(cityId)
            .associateBy { it.toGroupId() }

        val spontanUserNames = buildSpontanUserNameMap(cityId)

        return groupIds
            .map { groupId ->
                val entity = assignments[groupId]
                entity?.toSpontanGroupAssignment(spontanUserNames[entity.spontanUser?.id])
                    ?: SpontanGroupAssignment(cityId = cityId, groupId = groupId)
            }
            .filter { group ->
                accessibleIds == null || group.id in accessibleIds
            }
    }

    @Transactional
    fun assignSpontan(cityId: Int, groupId: GroupId, spontanDefinitionId: Long?): SpontanGroupAssignment {
        val spontanDefinition = spontanDefinitionId?.let {
            spontanDefinitionRepository.findById(it).orElse(null)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Spontan definition not found")
        }

        val assignment = spontanGroupAssignmentService.getOrCreateAssignment(cityId, groupId)
        assignment.spontanDefinition = spontanDefinition
        spontanGroupAssignmentService.addAssignmentEntity(assignment)

        return assignment.toSpontanGroupAssignment()
    }

    @Transactional
    fun setJudgeCount(cityId: Int, groupId: GroupId, judgeCount: Int): SpontanGroupAssignment {
        val assignment = spontanGroupAssignmentService.getOrCreateAssignment(cityId, groupId)
        assignment.judgeCount = judgeCount
        spontanGroupAssignmentService.addAssignmentEntity(assignment)

        return assignment.toSpontanGroupAssignment()
    }

    private fun validateFields(fields: List<SpontanFieldEntry>) {
        for (field in fields) {
            if (field.fieldType == SpontanFieldType.EXPRESSION) {
                val expr = field.expression
                    ?: throw ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Pole '${field.name}': brak wyrażenia"
                    )
                try {
                    Expression(expr).with("v", 1).evaluate()
                } catch (e: Exception) {
                    throw ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Pole '${field.name}': nieprawidłowe wyrażenie: ${e.message}"
                    )
                }
            }
        }
    }

    private fun buildSpontanUserNameMap(cityId: Int): Map<Long, String> {
        val spontanUsers = spontanUserRepository.findAllByCityId(cityId)
        return spontanUsers.mapNotNull { su ->
            val user = userRepository.findById(su.userId).orElse(null)
            if (su.id != null && user?.name != null) su.id!! to user.name!! else null
        }.toMap()
    }
}
