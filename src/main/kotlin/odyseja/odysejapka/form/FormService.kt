package odyseja.odysejapka.form

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(
    private val formEntryRepository: FormEntryRepository,
    private val formProblemRepository: FormProblemRepository,
    private val teamResultEntryRepository: TeamResultEntryRepository,
    private val teamFormService: TeamFormService,
    private val judgeCountService: JudgeCountService,
    private val teamResultService: TeamResultService,
) {

    @Transactional
    fun setFormEntries(problem: Int, form: ProblemForm) {
        val existing = formEntryRepository.findByProblem(problem)
        val existingById = existing.associateBy { it.id }
        val existingByCategory = existing.groupBy { it.formCategory }

        purgeMissingEntries(existingByCategory, form)
        val toPersist = buildEntitiesToPersist(problem, form, existingById)
        formEntryRepository.saveAll(toPersist)
        judgeCountService.setJudgesCount(problem, form.smallJudgesTeam, form.bigJudgesTeam)
    }

    private fun purgeMissingEntries(
        existingByCategory: Map<FormEntryEntity.FormCategory, List<FormEntryEntity>>,
        form: ProblemForm
    ) {
        val dtIds = FormEntryEntityConverter.collectAllIds(form.dtEntries)
        val styleIds = FormEntryEntityConverter.collectStyleIds(form.styleEntries)
        val penaltyIds = FormEntryEntityConverter.collectPenaltyIds(form.penaltyEntries)

        purgeMissing(dtIds, FormEntryEntity.FormCategory.DT, existingByCategory)
        purgeMissing(styleIds, FormEntryEntity.FormCategory.STYLE, existingByCategory)
        purgeMissing(penaltyIds, FormEntryEntity.FormCategory.PENALTY, existingByCategory)
    }

    private fun purgeMissing(
        ids: Set<Long>,
        category: FormEntryEntity.FormCategory,
        existingByCategory: Map<FormEntryEntity.FormCategory, List<FormEntryEntity>>
    ) {
        val toDelete = existingByCategory[category].orEmpty()
            .filter { it.id !in ids }
        if (toDelete.isEmpty()) return

        toDelete.forEach { teamResultEntryRepository.deleteAllByFormEntryEntity(it) }
        formEntryRepository.deleteAll(toDelete)
    }

    private fun buildEntitiesToPersist(
        problem: Int,
        form: ProblemForm,
        existingById: Map<Long, FormEntryEntity>
    ): List<FormEntryEntity> {
        return buildList {
            addAll(buildDtEntities(problem, form, existingById))
            addAll(buildStyleEntities(problem, form, existingById))
            addAll(buildPenaltyEntities(problem, form, existingById))
        }
    }

    private fun buildDtEntities(
        problem: Int,
        form: ProblemForm,
        existingById: Map<Long, FormEntryEntity>
    ) = FormEntryEntityConverter.flattenLongTermToEntities(
        problem, form.dtEntries, FormEntryEntity.FormCategory.DT, existingById
    )

    private fun buildStyleEntities(
        problem: Int,
        form: ProblemForm,
        existingById: Map<Long, FormEntryEntity>
    ) = FormEntryEntityConverter.flattenStyleToEntities(
        problem, form.styleEntries, FormEntryEntity.FormCategory.STYLE, existingById
    )

    private fun buildPenaltyEntities(
        problem: Int,
        form: ProblemForm,
        existingById: Map<Long, FormEntryEntity>
    ) = FormEntryEntityConverter.flattenPenaltyToEntities(
        problem, form.penaltyEntries, FormEntryEntity.FormCategory.PENALTY, existingById
    )


    fun getProblemForm(problem: Int): ProblemForm {
        val entries = formEntryRepository.findByProblem(problem)
        val problemEntities = formProblemRepository.findByProblem(problem)
        val smallJudgesTeam = extractJudgeTeamIds(problemEntities, 1)
        val bigJudgesTeam = extractJudgeTeamIds(problemEntities, 2)

        return ProblemForm(
            dtEntries = reconstructDtEntries(entries),
            styleEntries = reconstructStyleEntries(entries),
            penaltyEntries = reconstructPenaltyEntries(entries),
            smallJudgesTeam = smallJudgesTeam,
            bigJudgesTeam = bigJudgesTeam
        )
    }

    private fun extractJudgeTeamIds(problemEntities: List<FormProblemEntity>, judgeCount: Int): List<Int>? {
        val ids = problemEntities
            .filter { it.judgeCount == judgeCount }
            .mapNotNull { it.city?.id }
        return ids.ifEmpty { null }
    }

    private fun reconstructDtEntries(entries: List<FormEntryEntity>) =
        FormEntryEntityConverter.reconstructLongTermFromEntities(
            entries.filter { it.formCategory == FormEntryEntity.FormCategory.DT }
        )

    private fun reconstructStyleEntries(entries: List<FormEntryEntity>) =
        FormEntryEntityConverter.reconstructStyleFromEntities(
            entries.filter { it.formCategory == FormEntryEntity.FormCategory.STYLE }
        )

    private fun reconstructPenaltyEntries(entries: List<FormEntryEntity>) =
        FormEntryEntityConverter.reconstructPenaltyFromEntities(
            entries.filter { it.formCategory == FormEntryEntity.FormCategory.PENALTY }
        )

    fun getJudgeCount(problem: Int, cityId: Int): JudgeCountResponse {
        return judgeCountService.getJudgeCount(problem, cityId)
    }

    @Transactional
    fun setTeamResults(performanceId: Int, request: PerformanceResultsRequest) {
        teamResultService.setTeamResults(performanceId, request)
    }

    fun getTeamForm(performanceId: Int): TeamForm {
        return teamFormService.getTeamForm(performanceId)
    }
}
