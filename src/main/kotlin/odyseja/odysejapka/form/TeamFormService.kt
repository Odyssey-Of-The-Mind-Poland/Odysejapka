package odyseja.odysejapka.form

import jakarta.transaction.Transactional
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service

@Service
class TeamFormService(
    private val teamResultEntryRepository: TeamResultEntryRepository,
    private val performanceRepository: PerformanceRepository,
    private val formEntryRepository: FormEntryRepository,
    private val formProblemRepository: FormProblemRepository,
) {

    @Transactional
    fun getTeamForm(performanceId: Int): TeamForm {
        val performance = performanceRepository.findById(performanceId).get()
        val problem = performance.problemEntity.id
        val city = performance.cityEntity
        val template = formEntryRepository.findByProblem(problem)
        val results = teamResultEntryRepository.findByPerformanceEntityId(performanceId)
        
        val judgeCount = formProblemRepository.findByProblemAndCity(problem, city)?.judgeCount ?: 1
        
        val dtEntities = template
            .filter { it.formCategory == FormEntryEntity.FormCategory.DT && it.parent == null }
            .sortedBy { it.orderIndex }
        val styleEntities = template
            .filter { it.formCategory == FormEntryEntity.FormCategory.STYLE && it.parent == null }
            .sortedBy { it.orderIndex }
        val penaltyEntities = template
            .filter { it.formCategory == FormEntryEntity.FormCategory.PENALTY && it.parent == null }
            .sortedBy { it.orderIndex }
        
        val childrenByParent = buildChildrenMap(template)
        
        val dtEntries = getDtResults(dtEntities, results, childrenByParent, judgeCount)
        val styleEntries = getStyleResults(styleEntities, results, childrenByParent, judgeCount)
        val penaltyEntries = getPenaltyResults(penaltyEntities, results, childrenByParent, judgeCount)
        
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

    private fun buildChildrenMap(entities: List<FormEntryEntity>): Map<Long, List<FormEntryEntity>> {
        return entities.filter { it.parent != null }
            .groupBy { it.parent!!.id }
            .mapValues { (_, children) -> children.sortedBy { it.orderIndex } }
    }

    private fun createJudgeMap(
        judgeCount: Int,
        resultEntries: List<TeamResultEntryEntity>,
        entryId: Long,
        judgeType: JudgeType
    ): Map<Int, Long?> {
        val resultMap = resultEntries
            .filter { it.formEntryEntity?.id == entryId && it.judgeType == judgeType }
            .groupBy { it.judge }
            .mapValues { (_, entries) -> entries.sumOf { it.result } }
        
        return (1..judgeCount).associateWith { judgeIndex ->
            resultMap[judgeIndex]
        }
    }

    private fun getDtResults(
        templateEntries: List<FormEntryEntity>,
        resultEntries: List<TeamResultEntryEntity>,
        childrenByParent: Map<Long, List<FormEntryEntity>>,
        judgeCount: Int
    ): List<TeamForm.DtTeamFormEntry> {
        return templateEntries.map { templateEntry ->
            val entry = templateEntry.toLongTermFormEntry(childrenByParent)
            val judgesA = createJudgeMap(judgeCount, resultEntries, templateEntry.id, JudgeType.DT_A)
            val judgesB = createJudgeMap(judgeCount, resultEntries, templateEntry.id, JudgeType.DT_B)
            
            // Get noElement from any result entry for this form entry (it's the same for all judges)
            val noElement = resultEntries
                .firstOrNull { it.formEntryEntity?.id == templateEntry.id }
                ?.noElement
                ?: false

            TeamForm.DtTeamFormEntry(
                entry = entry,
                results = mapOf(
                    JudgeType.DT_A to judgesA,
                    JudgeType.DT_B to judgesB
                ),
                noElement = noElement
            )
        }
    }

    private fun getStyleResults(
        templateEntries: List<FormEntryEntity>,
        resultEntries: List<TeamResultEntryEntity>,
        childrenByParent: Map<Long, List<FormEntryEntity>>,
        judgeCount: Int
    ): List<TeamForm.StyleTeamFormEntry> {
        return templateEntries.map { templateEntry ->
            val entry = templateEntry.toStyleFormEntry(childrenByParent)
            val styleJudge = createJudgeMap(judgeCount, resultEntries, templateEntry.id, JudgeType.STYLE)

            TeamForm.StyleTeamFormEntry(
                entry = entry,
                results = mapOf(JudgeType.STYLE to styleJudge)
            )
        }
    }

    private fun getPenaltyResults(
        templateEntries: List<FormEntryEntity>,
        resultEntries: List<TeamResultEntryEntity>,
        childrenByParent: Map<Long, List<FormEntryEntity>>,
        judgeCount: Int
    ): List<TeamForm.PenaltyTeamFormEntry> {
        return templateEntries.map { templateEntry ->
            val entry = templateEntry.toPenaltyFormEntry(childrenByParent)
            
            val penaltyResult = resultEntries
                .firstOrNull { it.formEntryEntity?.id == templateEntry.id }
            
            val result = penaltyResult?.result

            TeamForm.PenaltyTeamFormEntry(
                entry = entry,
                result = result
            )
        }
    }
}