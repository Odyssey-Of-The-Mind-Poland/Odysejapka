package odyseja.odysejapka.form

import odyseja.odysejapka.city.CityRepository
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(
    private val formEntryRepository: FormEntryRepository,
    private val formProblemRepository: FormProblemRepository,
    private val teamResultEntryRepository: TeamResultEntryRepository,
    private val performanceRepository: PerformanceRepository,
    private val cityRepository: CityRepository,
    private val teamFormService: TeamFormService,
) {

    @Transactional
    fun setFormEntries(problem: Int, form: ProblemForm) {
        val existing = formEntryRepository.findByProblem(problem)
        val existingById = existing.associateBy { it.id }
        val existingByCategory = existing.groupBy { it.formCategory }

        fun purgeMissing(ids: Set<Long>, category: FormEntryEntity.FormCategory) {
            val toDelete = existingByCategory[category].orEmpty()
                .filter { it.id !in ids }

            if (toDelete.isNotEmpty()) {
                toDelete.forEach { teamResultEntryRepository.deleteAllByFormEntryEntity(it) }
                formEntryRepository.deleteAll(toDelete)
            }
        }

        val dtIds = FormEntryEntityConverter.collectAllIds(form.dtEntries)
        val styleIds = FormEntryEntityConverter.collectStyleIds(form.styleEntries)
        val penaltyIds = FormEntryEntityConverter.collectPenaltyIds(form.penaltyEntries)

        purgeMissing(dtIds, FormEntryEntity.FormCategory.DT)
        purgeMissing(styleIds, FormEntryEntity.FormCategory.STYLE)
        purgeMissing(penaltyIds, FormEntryEntity.FormCategory.PENALTY)

        val toPersist = buildList {
            addAll(FormEntryEntityConverter.flattenLongTermToEntities(problem, form.dtEntries, FormEntryEntity.FormCategory.DT, existingById))
            addAll(FormEntryEntityConverter.flattenStyleToEntities(problem, form.styleEntries, FormEntryEntity.FormCategory.STYLE, existingById))
            addAll(FormEntryEntityConverter.flattenPenaltyToEntities(problem, form.penaltyEntries, FormEntryEntity.FormCategory.PENALTY, existingById))
        }

        formEntryRepository.saveAll(toPersist)
    }


    fun getFormEntries(problem: Int): ProblemForm {
        val entries = formEntryRepository.findByProblem(problem)
        return ProblemForm(
            FormEntryEntityConverter.reconstructLongTermFromEntities(
                entries.filter { it.formCategory == FormEntryEntity.FormCategory.DT }
            ),
            FormEntryEntityConverter.reconstructStyleFromEntities(
                entries.filter { it.formCategory == FormEntryEntity.FormCategory.STYLE }
            ),
            FormEntryEntityConverter.reconstructPenaltyFromEntities(
                entries.filter { it.formCategory == FormEntryEntity.FormCategory.PENALTY }
            )
        )
    }

    @Transactional
    fun setJudgeCount(problem: Int, cityId: Int, count: Int): Int {
        val city = cityRepository.findFirstById(cityId)
        val problemEntity = formProblemRepository.findByProblemAndCity(problem, city) ?: FormProblemEntity().apply {
            this.problem = problem
            this.city = city
        }
        problemEntity.judgeCount = count
        formProblemRepository.save(problemEntity)
        return count
    }

    fun getJudgeCount(problem: Int, cityId: Int): Int {
        val city = cityRepository.findFirstById(cityId)
        return formProblemRepository.findByProblemAndCity(problem, city)?.judgeCount ?: 0
    }

    @Transactional
    fun setTeamResults(performanceId: Int, request: PerformanceResultsRequest) {
        val performance = performanceRepository.findById(performanceId)
            .orElseThrow { IllegalArgumentException("Performance $performanceId not found") }

        if (request.results.isEmpty()) return

        val entryIds = request.results.map { it.entryId }.toSet()
        val formEntries = formEntryRepository.findAllById(entryIds)
        val formEntryById = formEntries.associateBy { it.id }
        val missing = entryIds - formEntryById.keys
        if (missing.isNotEmpty()) {
            throw IllegalArgumentException("Unknown form entry id(s): $missing")
        }

        val toSave = mutableListOf<TeamResultEntryEntity>()
        request.results.forEach { r ->
            val existing = teamResultEntryRepository
                .findByPerformanceEntityIdAndFormEntryEntityIdAndJudge(performanceId, r.entryId, r.judge)

            if (existing != null) {
                if (existing.result != r.result) {
                    existing.result = r.result
                    toSave += existing
                }
            } else {
                val entity = TeamResultEntryEntity().apply {
                    performanceEntity = performance
                    formEntryEntity = formEntryById.getValue(r.entryId)
                    judge = r.judge
                    result = r.result
                }
                toSave += entity
            }
        }

        if (toSave.isNotEmpty()) {
            teamResultEntryRepository.saveAll(toSave)
        }
    }

    fun getTeamForm(performanceId: Int): TeamForm {
        return teamFormService.getTeamForm(performanceId)
    }
}
