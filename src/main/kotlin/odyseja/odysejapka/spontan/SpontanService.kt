package odyseja.odysejapka.spontan

import odyseja.odysejapka.city.CityRepository
import odyseja.odysejapka.timetable.PerformanceGroupService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class SpontanService(
    private val spontanDefinitionRepository: SpontanDefinitionRepository,
    private val spontanGroupAssignmentRepository: SpontanGroupAssignmentRepository,
    private val cityRepository: CityRepository,
    private val performanceGroupService: PerformanceGroupService
) {

    fun getAll(): List<SpontanDefinition> {
        return spontanDefinitionRepository.findAll().map { it.toDto() }
    }

    fun getById(id: Long): SpontanDefinition {
        val entity = spontanDefinitionRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return entity.toDto()
    }

    @Transactional
    fun create(definition: SpontanDefinition): SpontanDefinition {
        val entity = SpontanDefinitionEntity().apply {
            name = definition.name
            type = definition.type
            multiplier = definition.multiplier
            fields = if (definition.type == SpontanType.MANUAL) definition.fields else null
        }
        return spontanDefinitionRepository.save(entity).toDto()
    }

    @Transactional
    fun update(id: Long, definition: SpontanDefinition): SpontanDefinition {
        val entity = spontanDefinitionRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        entity.name = definition.name
        entity.type = definition.type
        entity.multiplier = definition.multiplier
        entity.fields = if (definition.type == SpontanType.MANUAL) definition.fields else null
        return spontanDefinitionRepository.save(entity).toDto()
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

        val groups = performanceGroupService.getPerformanceGroups(cityId)
        val assignments = spontanGroupAssignmentRepository.findByCityId(cityId)
            .associateBy { Triple(it.problem, it.age, it.league) }

        return groups.map { group ->
            val key = Triple(group.group.problem, group.group.age, group.group.league)
            val assignment = assignments[key]
            SpontanGroupAssignment(
                id = assignment?.id,
                cityId = cityId,
                problem = group.group.problem,
                age = group.group.age,
                league = group.group.league,
                spontanDefinitionId = assignment?.spontanDefinition?.id,
                spontanDefinitionName = assignment?.spontanDefinition?.name,
                spontanType = assignment?.spontanDefinition?.type,
                judgeCount = assignment?.judgeCount ?: 3
            )
        }
    }

    @Transactional
    fun assignSpontan(cityId: Int, problem: Int, age: Int, league: String?, spontanDefinitionId: Long?): SpontanGroupAssignment {
        val city = cityRepository.findById(cityId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")

        val spontanDefinition = spontanDefinitionId?.let {
            spontanDefinitionRepository.findById(it).orElse(null)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Spontan definition not found")
        }

        val assignment = spontanGroupAssignmentRepository
            .findByCityIdAndProblemAndAgeAndLeague(cityId, problem, age, league)
            ?: SpontanGroupAssignmentEntity().apply {
                this.city = city
                this.problem = problem
                this.age = age
                this.league = league
            }

        assignment.spontanDefinition = spontanDefinition
        val saved = spontanGroupAssignmentRepository.save(assignment)

        return SpontanGroupAssignment(
            id = saved.id,
            cityId = cityId,
            problem = problem,
            age = age,
            league = league,
            spontanDefinitionId = saved.spontanDefinition?.id,
            spontanDefinitionName = saved.spontanDefinition?.name,
            spontanType = saved.spontanDefinition?.type,
            judgeCount = saved.judgeCount
        )
    }

    @Transactional
    fun setJudgeCount(cityId: Int, problem: Int, age: Int, league: String?, judgeCount: Int): SpontanGroupAssignment {
        val city = cityRepository.findById(cityId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "City not found")

        val assignment = spontanGroupAssignmentRepository
            .findByCityIdAndProblemAndAgeAndLeague(cityId, problem, age, league)
            ?: SpontanGroupAssignmentEntity().apply {
                this.city = city
                this.problem = problem
                this.age = age
                this.league = league
            }

        assignment.judgeCount = judgeCount
        val saved = spontanGroupAssignmentRepository.save(assignment)

        return SpontanGroupAssignment(
            id = saved.id,
            cityId = cityId,
            problem = problem,
            age = age,
            league = league,
            spontanDefinitionId = saved.spontanDefinition?.id,
            spontanDefinitionName = saved.spontanDefinition?.name,
            spontanType = saved.spontanDefinition?.type,
            judgeCount = saved.judgeCount
        )
    }

    private fun SpontanDefinitionEntity.toDto() = SpontanDefinition(
        id = id,
        name = name,
        type = type,
        multiplier = multiplier,
        fields = fields ?: emptyList()
    )
}
