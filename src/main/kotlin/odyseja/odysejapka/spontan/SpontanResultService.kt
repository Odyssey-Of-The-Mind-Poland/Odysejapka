package odyseja.odysejapka.spontan

import odyseja.odysejapka.timetable.TimeTableService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class SpontanResultService(
    private val spontanResultRepository: SpontanResultRepository,
    private val spontanGroupAssignmentService: SpontanGroupAssignmentService,
    private val timeTableService: TimeTableService
) {

    private val scoreCalculator = SpontanScoreCalculator()

    fun getGroupTeams(cityId: Int, groupId: GroupId): SpontanGroupTeams {
        val assignment = spontanGroupAssignmentService
            .getAssignmentEntity(cityId, groupId.problem, groupId.age, groupId.league)

        val definition = assignment.spontanDefinition
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No spontan definition assigned")

        val performances = timeTableService.getPerformanceEntitiesByCity(cityId)
            .filter {
                !it.isExcludedFromScoring() &&
                it.problemEntity.id == groupId.problem &&
                it.ageEntity.id == groupId.age &&
                (it.league ?: "") == groupId.league
            }
            .map { it.toPerformance() }

        val performanceIds = performances.map { it.id }
        val resultsMap = spontanResultRepository.findAllByPerformanceIdIn(performanceIds)
            .associateBy { it.performanceId }

        val teams = performances
            .sortedBy { it.spontan }
            .map { perf ->
                val resultEntity = resultsMap[perf.id]
                val results = resultEntity?.results ?: SpontanResults()
                SpontanTeamResult(
                    performanceId = perf.id,
                    team = perf.team,
                    spontanHour = perf.spontan,
                    verbalEntries = results.verbalEntries,
                    manualJudgeEntries = results.manualJudgeEntries,
                    manualEntries = results.manualEntries,
                    rawSpontan = resultEntity?.rawSpontan
                )
            }

        return SpontanGroupTeams(
            spontanDefinition = definition.toSpontanDefinition(),
            judgeCount = assignment.judgeCount,
            teams = teams
        )
    }

    @Transactional
    fun setResults(performanceId: Int, request: SpontanResultsRequest): SpontanTeamResult {
        val performance = timeTableService.getPerformanceEntity(performanceId)

        val cityId = performance.cityEntity.id
        val groupId = GroupId(
            problem = performance.problemEntity.id,
            age = performance.ageEntity.id,
            league = performance.league ?: ""
        )

        val assignment = spontanGroupAssignmentService
            .getAssignmentEntity(cityId, groupId.problem, groupId.age, groupId.league)

        val definition = assignment.spontanDefinition
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No spontan definition assigned")

        val teamResults = spontanResultRepository.findByPerformanceId(performanceId)
            ?: SpontanResultEntity().apply { this.performanceId = performanceId }

        val results = SpontanResults(
            verbalEntries = request.verbalEntries,
            manualJudgeEntries = request.manualJudgeEntries,
            manualEntries = request.manualEntries
        )
        teamResults.results = results

        val score = scoreCalculator.calculateScore(definition.toSpontanDefinition(), results, assignment.judgeCount)
        teamResults.rawSpontan = score

        spontanResultRepository.save(teamResults)

        return SpontanTeamResult(
            performanceId = performanceId,
            team = performance.team,
            spontanHour = performance.spontan,
            verbalEntries = request.verbalEntries,
            manualJudgeEntries = request.manualJudgeEntries,
            manualEntries = request.manualEntries,
            rawSpontan = score
        )
    }

}
