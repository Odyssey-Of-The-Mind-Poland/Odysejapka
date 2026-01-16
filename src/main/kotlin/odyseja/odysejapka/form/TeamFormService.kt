package odyseja.odysejapka.form

import jakarta.transaction.Transactional
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service

@Service
class TeamFormService(
    private val teamResultRepository: TeamResultRepository,
    private val performanceRepository: PerformanceRepository,
    private val formProblemRepository: FormProblemRepository,
    private val cityFormJudgesRepository: CityFormJudgesRepository,
) {

    @Transactional
    fun getTeamForm(performanceId: Int): TeamForm {
        val performance = performanceRepository.findById(performanceId).get()
        val problem = performance.problemEntity.id
        val city = performance.cityEntity

        val judgeCount = cityFormJudgesRepository.findByProblemAndCity(problem, city)?.judgeCount ?: 1

        val formEntity = formProblemRepository.findByProblem(problem)
            ?: return TeamForm(
                performanceId = performanceId,
                teamName = performance.team,
                cityName = city.name,
                problem = problem,
                age = performance.ageEntity.id,
                isFo = city.name.lowercase().contains("finał") || city.name.lowercase().contains("final"),
                dtEntries = emptyList(),
                styleEntries = emptyList(),
                penaltyEntries = emptyList()
            )

        val form = formEntity.form ?: return TeamForm(
            performanceId = performanceId,
            teamName = performance.team,
            cityName = city.name,
            problem = problem,
            age = performance.ageEntity.id,
            isFo = city.name.lowercase().contains("finał") || city.name.lowercase().contains("final"),
            dtEntries = emptyList(),
            styleEntries = emptyList(),
            penaltyEntries = emptyList()
        )

        val resultEntity = teamResultRepository.findByPerformanceId(performanceId)
        val results = resultEntity?.results?.results ?: emptyList()

        val dtEntries = getDtResults(form.dtEntries, results, judgeCount)
        val styleEntries = getStyleResults(form.styleEntries, results, judgeCount)
        val penaltyEntries = getPenaltyResults(form.penaltyEntries, results)

        val isFo = city.name.lowercase().contains("finał") || city.name.lowercase().contains("final")

        return TeamForm(
            performanceId = performanceId,
            teamName = performance.team,
            cityName = city.name,
            problem = problem,
            age = performance.ageEntity.id,
            isFo = isFo,
            dtEntries = dtEntries,
            styleEntries = styleEntries,
            penaltyEntries = penaltyEntries
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

            val noElement = results
                .firstOrNull { it.entryId == entryId }
                ?.noElement
                ?: false

            val nestedEntries = getNestedEntries(templateEntry, results, judgeCount)

            TeamForm.DtTeamFormEntry(
                entry = templateEntry,
                results = mapOf(
                    JudgeType.DT_A to judgesA,
                    JudgeType.DT_B to judgesB
                ),
                noElement = noElement,
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
}