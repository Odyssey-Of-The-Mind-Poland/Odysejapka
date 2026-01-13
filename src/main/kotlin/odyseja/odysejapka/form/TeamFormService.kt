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
        
        return TeamForm(performanceId, dtEntries, styleEntries, penaltyEntries)
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
        judgeOffset: Int = 0
    ): Map<Int, Long?> {
        val resultMap = resultEntries
            .filter { it.formEntryEntity?.id == entryId }
            .groupBy { it.judge }
            .mapValues { (_, entries) -> entries.sumOf { it.result } }
        
        return (1..judgeCount).associateWith { judgeIndex ->
            resultMap[judgeIndex + judgeOffset]
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
            val judgesA = createJudgeMap(judgeCount, resultEntries, templateEntry.id, judgeOffset = 0)
            val judgesB = createJudgeMap(judgeCount, resultEntries, templateEntry.id, judgeOffset = judgeCount)

            TeamForm.DtTeamFormEntry(
                entry = entry,
                judgesA = judgesA,
                judgesB = judgesB
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
            val styleJudge = createJudgeMap(judgeCount, resultEntries, templateEntry.id)

            TeamForm.StyleTeamFormEntry(
                entry = entry,
                styleJudge = styleJudge
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
            val penalty = createJudgeMap(judgeCount, resultEntries, templateEntry.id)

            TeamForm.PenaltyTeamFormEntry(
                entry = entry,
                penalty = penalty
            )
        }
    }
}