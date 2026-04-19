package odyseja.odysejapka.form

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import odyseja.odysejapka.city.KonkursLevel
import odyseja.odysejapka.timetable.TimeTableService
import org.springframework.stereotype.Service

@Service
class TeamFormService(
    private val teamResultService: TeamResultService,
    private val timeTableService: TimeTableService,
    private val formProblemService: FormProblemService,
    private val judgeCountService: JudgeCountService
) {

    @Transactional
    fun getTeamForm(performanceId: Int): TeamForm {
        val resultEntity = try {
            teamResultService.getTeamResult(performanceId)
        } catch (_: EntityNotFoundException) {
            TeamResultEntity().apply { this.performanceId = performanceId }
        }
        val approved = resultEntity.approved
        val ranatra = resultEntity.ranatra
        val results = resultEntity.results?.results ?: emptyList()
        val weightHeldResults = resultEntity.results?.weightHeldResults ?: emptyMap()
        val performance = timeTableService.getPerformanceEntity(performanceId)
        val problem = performance.problemEntity.id
        val city = performance.cityEntity
        val isFo = city.level == KonkursLevel.FINAL

        val judgeCount = judgeCountService.getJudgeCountByProblemAndCity(problem, city.id).judgeCount

        val emptyForm = TeamForm(
            performanceId = performanceId,
            teamName = performance.team,
            cityName = city.name,
            problem = problem,
            age = performance.ageEntity.id,
            isFo = isFo,
            performanceAt = "",
            performanceTime = "",
            dtEntries = emptyList(),
            styleEntries = emptyList(),
            penaltyEntries = emptyList(),
            approved = approved,
            judgeCount = judgeCount,
            ranatra = ranatra
        )

        val formEntity = formProblemService.findByProblem(problem)
        val form = formEntity.form ?: return emptyForm

        val dtEntries = getDtResults(form.dtEntries, results, judgeCount)
        val styleEntries = getStyleResults(form.styleEntries, results, judgeCount)
        val penaltyEntries = getPenaltyResults(form.penaltyEntries, results)
        val weightHeldEntries = if (problem == 4) getWeightHeldResults(weightHeldResults) else emptyList()

        val performanceAt = resultEntity.performanceAt ?: ""
        val performanceTime = resultEntity.performanceTime ?: ""

        return TeamForm(
            performanceId = performanceId,
            teamName = performance.team,
            cityName = city.name,
            problem = problem,
            age = performance.ageEntity.id,
            isFo = isFo,
            performanceAt = performanceAt,
            performanceTime = performanceTime,
            dtEntries = dtEntries,
            styleEntries = styleEntries,
            penaltyEntries = penaltyEntries,
            weightHeldEntries = weightHeldEntries,
            approved = approved,
            judgeCount = judgeCount,
            ranatra = ranatra
        )
    }

    private fun createJudgeMap(
        judgeCount: Int,
        results: List<PerformanceResultsRequest.PerformanceResult>,
        entryId: Long,
        judgeType: JudgeType
    ): Map<Int, Long?> {
        val resultMap = results
            .filter { it.entryId == entryId && it.judgeType == judgeType }
            .groupBy { it.judge }
            .mapValues { (_, entries) -> entries.sumOf { it.result } }

        return (1..judgeCount).associateWith { judgeIndex ->
            resultMap[judgeIndex]
        }
    }

    private fun getDtResults(
        templateEntries: List<LongTermFormEntry>,
        results: List<PerformanceResultsRequest.PerformanceResult>,
        judgeCount: Int
    ): List<TeamForm.DtTeamFormEntry> {
        return templateEntries.map { templateEntry ->
            val entryId = templateEntry.id ?: return@map null
            val judgesA = createJudgeMap(judgeCount, results, entryId, JudgeType.DT_A)
            val judgesB = createJudgeMap(judgeCount, results, entryId, JudgeType.DT_B)

            val firstResult = results.firstOrNull { it.entryId == entryId }
            val noElement = firstResult?.noElement ?: false
            val noElementComment = if (noElement) firstResult?.comment else null

            val nestedEntries = getNestedEntries(templateEntry, results, judgeCount)

            TeamForm.DtTeamFormEntry(
                entry = templateEntry,
                results = mapOf(
                    JudgeType.DT_A to judgesA,
                    JudgeType.DT_B to judgesB
                ),
                noElement = noElement,
                noElementComment = noElementComment,
                nestedEntries = nestedEntries
            )
        }.filterNotNull()
    }

    private fun getNestedEntries(
        entry: LongTermFormEntry,
        results: List<PerformanceResultsRequest.PerformanceResult>,
        judgeCount: Int
    ): List<TeamForm.DtTeamFormEntry> = if (entry.entries.isNotEmpty() &&
        (entry.type == LongTermFormEntry.EntryType.SCORING_GROUP ||
                entry.type == LongTermFormEntry.EntryType.SECTION)
    ) {
        getDtResults(entry.entries, results, judgeCount)
    } else {
        emptyList()
    }

    private fun getStyleResults(
        templateEntries: List<StyleFormEntry>,
        results: List<PerformanceResultsRequest.PerformanceResult>,
        judgeCount: Int
    ): List<TeamForm.StyleTeamFormEntry> {
        return templateEntries.map { templateEntry ->
            val entryId = templateEntry.id ?: return@map null
            val styleJudge = createJudgeMap(judgeCount, results, entryId, JudgeType.STYLE)
            
            val styleName = results
                .firstOrNull { it.entryId == entryId && it.judgeType == JudgeType.STYLE }
                ?.styleName

            TeamForm.StyleTeamFormEntry(
                entry = templateEntry,
                results = mapOf(JudgeType.STYLE to styleJudge),
                styleName = styleName
            )
        }.filterNotNull()
    }

    private fun getPenaltyResults(
        templateEntries: List<PenaltyFormEntry>,
        results: List<PerformanceResultsRequest.PerformanceResult>,
    ): List<TeamForm.PenaltyTeamFormEntry> {
        return templateEntries.map { templateEntry ->
            val entryId = templateEntry.id ?: return@map null

            val penaltyResult = results
                .firstOrNull { it.entryId == entryId }

            val result = penaltyResult?.result
            val zeroBalsa = penaltyResult?.zeroBalsa
            val comment = penaltyResult?.comment

            TeamForm.PenaltyTeamFormEntry(
                entry = templateEntry,
                result = result,
                zeroBalsa = zeroBalsa,
                comment = comment
            )
        }.filterNotNull()
    }

    private fun getWeightHeldResults(
        weightHeldResults: Map<Long, List<Double>>
    ): List<TeamForm.WeightHeldTeamFormEntry> {
        val entry = WeightHeldFormEntry.forProblem4()
        val weights = weightHeldResults[WeightHeldFormEntry.PROBLEM_4_ENTRY_ID] ?: emptyList()
        return listOf(
            TeamForm.WeightHeldTeamFormEntry(
                entry = entry,
                weights = weights
            )
        )
    }
}