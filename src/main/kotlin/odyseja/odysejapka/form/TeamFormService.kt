package odyseja.odysejapka.form

import jakarta.transaction.Transactional
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service

@Service
class TeamFormService(
    private val teamResultEntryRepository: TeamResultEntryRepository,
    private val performanceRepository: PerformanceRepository,
    private val formEntryRepository: FormEntryRepository,
) {

    @Transactional
    fun getTeamForm(performanceId: Int): TeamForm {
        val problem = performanceRepository.findById(performanceId)
        val template = formEntryRepository.findByProblem(problem.get().problemEntity.id)
        val results = teamResultEntryRepository.findByPerformanceEntityId(performanceId)
        
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
        
        val dtEntries = getDtResults(dtEntities, results, childrenByParent)
        val styleEntries = getStyleResults(styleEntities, results, childrenByParent)
        val penaltyEntries = getPenaltyResults(penaltyEntities, results, childrenByParent)
        
        return TeamForm(performanceId, dtEntries, styleEntries, penaltyEntries)
    }

    private fun buildChildrenMap(entities: List<FormEntryEntity>): Map<Long, List<FormEntryEntity>> {
        return entities.filter { it.parent != null }
            .groupBy { it.parent!!.id }
            .mapValues { (_, children) -> children.sortedBy { it.orderIndex } }
    }

    private fun <T> getResults(
        templateEntries: List<FormEntryEntity>,
        resultEntries: List<TeamResultEntryEntity>,
        childrenByParent: Map<Long, List<FormEntryEntity>>,
        convertEntry: (FormEntryEntity, Map<Long, List<FormEntryEntity>>) -> T
    ): List<TeamForm.TeamFormEntry<T>> {
        return templateEntries.map { templateEntry ->
            val entry = convertEntry(templateEntry, childrenByParent)
            val judgeResults = resultEntries
                .filter { it.formEntryEntity?.id == templateEntry.id }
                .groupBy { it.judge }
                .mapValues { (_, entries) -> entries.sumOf { it.result } }

            TeamForm.TeamFormEntry(
                entry = entry,
                judgeResults = judgeResults
            )
        }
    }

    private fun getDtResults(
        templateEntries: List<FormEntryEntity>,
        resultEntries: List<TeamResultEntryEntity>,
        childrenByParent: Map<Long, List<FormEntryEntity>>
    ): List<TeamForm.TeamFormEntry<LongTermFormEntry>> {
        return getResults(templateEntries, resultEntries, childrenByParent) { entry, children ->
            entry.toLongTermFormEntry(children)
        }
    }

    private fun getStyleResults(
        templateEntries: List<FormEntryEntity>,
        resultEntries: List<TeamResultEntryEntity>,
        childrenByParent: Map<Long, List<FormEntryEntity>>
    ): List<TeamForm.TeamFormEntry<StyleFormEntry>> {
        return getResults(templateEntries, resultEntries, childrenByParent) { entry, children ->
            entry.toStyleFormEntry(children)
        }
    }

    private fun getPenaltyResults(
        templateEntries: List<FormEntryEntity>,
        resultEntries: List<TeamResultEntryEntity>,
        childrenByParent: Map<Long, List<FormEntryEntity>>
    ): List<TeamForm.TeamFormEntry<PenaltyFormEntry>> {
        return getResults(templateEntries, resultEntries, childrenByParent) { entry, children ->
            entry.toPenaltyFormEntry(children)
        }
    }
}