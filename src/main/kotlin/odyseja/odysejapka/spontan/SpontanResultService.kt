package odyseja.odysejapka.spontan

import odyseja.odysejapka.timetable.PerformanceGroupService
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class SpontanResultService(
    private val spontanResultRepository: SpontanResultRepository,
    private val spontanGroupAssignmentRepository: SpontanGroupAssignmentRepository,
    private val spontanDefinitionRepository: SpontanDefinitionRepository,
    private val performanceRepository: PerformanceRepository,
    private val performanceGroupService: PerformanceGroupService
) {

    fun getGroupTeams(cityId: Int, problem: Int, age: Int, league: String?): SpontanGroupTeams {
        val assignment = spontanGroupAssignmentRepository
            .findByCityIdAndProblemAndAgeAndLeague(cityId, problem, age, league)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No spontan assignment for this group")

        val definition = assignment.spontanDefinition
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No spontan definition assigned")

        val definitionDto = SpontanDefinition(
            id = definition.id,
            name = definition.name,
            type = definition.type,
            multiplier = definition.multiplier,
            fields = definition.fields ?: emptyList()
        )

        val groups = performanceGroupService.getPerformanceGroups(cityId)
        val matchingGroup = groups.find {
            it.group.problem == problem && it.group.age == age && it.group.league == league
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found")

        val performanceIds = matchingGroup.performances.map { it.id }
        val resultsMap = spontanResultRepository.findAllByPerformanceIdIn(performanceIds)
            .associateBy { it.performanceId }

        val teams = matchingGroup.performances
            .sortedBy { it.spontan }
            .map { perf ->
                val resultEntity = resultsMap[perf.id]
                SpontanTeamResult(
                    performanceId = perf.id,
                    team = perf.team,
                    spontanHour = perf.spontan,
                    entries = resultEntity?.results ?: emptyList(),
                    rawSpontan = resultEntity?.rawSpontan
                )
            }

        return SpontanGroupTeams(
            spontanDefinition = definitionDto,
            judgeCount = assignment.judgeCount,
            teams = teams
        )
    }

    @Transactional
    fun setResults(performanceId: Int, request: SpontanResultsRequest): SpontanTeamResult {
        val performance = performanceRepository.findById(performanceId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Performance not found")

        val cityId = performance.cityEntity.id
        val problem = performance.problemEntity.id
        val age = performance.ageEntity.id
        val league = performance.league

        val assignment = spontanGroupAssignmentRepository
            .findByCityIdAndProblemAndAgeAndLeague(cityId, problem, age, league)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No spontan assignment for this group")

        val definition = assignment.spontanDefinition
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No spontan definition assigned")

        val entity = spontanResultRepository.findByPerformanceId(performanceId)
            ?: SpontanResultEntity().apply { this.performanceId = performanceId }

        entity.results = request.entries

        val score = calculateScore(definition, request.entries, assignment.judgeCount)
        entity.rawSpontan = score

        spontanResultRepository.save(entity)

        return SpontanTeamResult(
            performanceId = performanceId,
            team = performance.team,
            spontanHour = performance.spontan,
            entries = request.entries,
            rawSpontan = score
        )
    }

    fun calculateScore(
        definition: SpontanDefinitionEntity,
        entries: List<SpontanResultEntry>,
        judgeCount: Int
    ): Double {
        if (entries.isEmpty()) return 0.0

        return when (definition.type) {
            SpontanType.VERBAL -> calculateVerbalScore(entries, judgeCount)
            SpontanType.MANUAL -> calculateManualScore(definition, entries, judgeCount)
        }
    }

    /**
     * Verbal scoring:
     * - Per judge: creative * 5 + common
     * - Average across all judges
     */
    private fun calculateVerbalScore(entries: List<SpontanResultEntry>, judgeCount: Int): Double {
        if (judgeCount == 0) return 0.0

        val byJudge = entries.groupBy { it.judge }
        var total = 0.0

        for ((_, judgeEntries) in byJudge) {
            val creative = judgeEntries.find { it.field == "creative" }?.value ?: 0.0
            val common = judgeEntries.find { it.field == "common" }?.value ?: 0.0
            total += creative * 5 + common
        }

        return total / judgeCount
    }

    /**
     * Manual scoring:
     * - Sum of creativity + teamwork divided by judge count
     * - Each field value * field multiplier
     */
    private fun calculateManualScore(
        definition: SpontanDefinitionEntity,
        entries: List<SpontanResultEntry>,
        judgeCount: Int
    ): Double {
        if (judgeCount == 0) return 0.0

        val fields = definition.fields ?: emptyList()
        val fieldMultipliers = fields.associate { it.name to it.multiplier }

        val byJudge = entries.groupBy { it.judge }
        var judgeTotal = 0.0

        for ((_, judgeEntries) in byJudge) {
            val creativity = judgeEntries.find { it.field == "creativity" }?.value ?: 0.0
            val teamwork = judgeEntries.find { it.field == "teamwork" }?.value ?: 0.0
            judgeTotal += creativity + teamwork
        }

        val judgeScore = judgeTotal / judgeCount

        var fieldScore = 0.0
        for (entry in entries) {
            val multiplier = fieldMultipliers[entry.field]
            if (multiplier != null) {
                fieldScore += entry.value * multiplier
            }
        }

        return judgeScore + fieldScore
    }
}
