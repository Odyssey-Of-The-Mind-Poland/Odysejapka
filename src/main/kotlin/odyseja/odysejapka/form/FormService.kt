package odyseja.odysejapka.form

import jakarta.persistence.EntityNotFoundException
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
            addAll(
                FormEntryEntityConverter.flattenLongTermToEntities(
                    problem,
                    form.dtEntries,
                    FormEntryEntity.FormCategory.DT,
                    existingById
                )
            )
            addAll(
                FormEntryEntityConverter.flattenStyleToEntities(
                    problem,
                    form.styleEntries,
                    FormEntryEntity.FormCategory.STYLE,
                    existingById
                )
            )
            addAll(
                FormEntryEntityConverter.flattenPenaltyToEntities(
                    problem,
                    form.penaltyEntries,
                    FormEntryEntity.FormCategory.PENALTY,
                    existingById
                )
            )
        }

        formEntryRepository.saveAll(toPersist)

        setJudgesCount(problem, form.smallJudgesTeam, form.bigJudgesTeam)
    }


    fun getProblemForm(problem: Int): ProblemForm {
        val entries = formEntryRepository.findByProblem(problem)
        val problemEntities = formProblemRepository.findByProblem(problem)
        
        val smallJudgesTeam = problemEntities
            .filter { it.judgeCount == 1 }
            .mapNotNull { it.city?.id }
            .ifEmpty { null }
        
        val bigJudgesTeam = problemEntities
            .filter { it.judgeCount == 2 }
            .mapNotNull { it.city?.id }
            .ifEmpty { null }
        
        return ProblemForm(
            FormEntryEntityConverter.reconstructLongTermFromEntities(
                entries.filter { it.formCategory == FormEntryEntity.FormCategory.DT }
            ),
            FormEntryEntityConverter.reconstructStyleFromEntities(
                entries.filter { it.formCategory == FormEntryEntity.FormCategory.STYLE }
            ),
            FormEntryEntityConverter.reconstructPenaltyFromEntities(
                entries.filter { it.formCategory == FormEntryEntity.FormCategory.PENALTY }
            ),
            smallJudgesTeam = smallJudgesTeam,
            bigJudgesTeam = bigJudgesTeam
        )
    }

    @Transactional
    fun setJudgesCount(problem: Int, smallJudgesTeam: List<Int>?, bigJudgesTeam: List<Int>?) {
        smallJudgesTeam?.forEach { cityId ->
            val city = cityRepository.findFirstById(cityId)
            val problemEntity =
                formProblemRepository.findByProblemAndCity(problem, city) ?: FormProblemEntity.create(problem, city)
            problemEntity.judgeCount = 1
            formProblemRepository.save(problemEntity)
        }

        bigJudgesTeam?.forEach { cityId ->
            val city = cityRepository.findFirstById(cityId)
            val problemEntity =
                formProblemRepository.findByProblemAndCity(problem, city) ?: FormProblemEntity.create(problem, city)
            problemEntity.judgeCount = 2
            formProblemRepository.save(problemEntity)
        }
    }

    fun getJudgeCount(problem: Int, cityId: Int): JudgeCountResponse {
        val city = cityRepository.findFirstById(cityId)
        val problemEntity = formProblemRepository.findByProblemAndCity(problem, city)
            ?: throw EntityNotFoundException("No judge count set for problem $problem and city $cityId")
        return JudgeCountResponse(
            judgeCount = problemEntity.judgeCount
        )
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

        // Group results by entryId to extract noElement per entry
        val noElementByEntryId = request.results
            .groupBy { it.entryId }
            .mapValues { (_, results) -> results.firstOrNull()?.noElement ?: false }
        
        val toSave = mutableListOf<TeamResultEntryEntity>()
        request.results.forEach { r ->
            val existing = teamResultEntryRepository
                .findByPerformanceEntityIdAndFormEntryEntityIdAndJudgeTypeAndJudge(performanceId, r.entryId, r.judgeType, r.judge)

            if (existing != null) {
                var needsUpdate = false
                if (existing.result != r.result) {
                    existing.result = r.result
                    needsUpdate = true
                }
                val noElement = noElementByEntryId[r.entryId] ?: false
                if (existing.noElement != noElement) {
                    existing.noElement = noElement
                    needsUpdate = true
                }
                if (needsUpdate) {
                    toSave += existing
                }
            } else {
                val entity = TeamResultEntryEntity().apply {
                    performanceEntity = performance
                    formEntryEntity = formEntryById.getValue(r.entryId)
                    judgeType = r.judgeType
                    judge = r.judge
                    result = r.result
                    noElement = noElementByEntryId[r.entryId] ?: false
                }
                toSave += entity
            }
        }
        
        // Update noElement on all existing result entries for entries in the request
        // This ensures all judge results for an entry have the same noElement flag
        noElementByEntryId.forEach { (entryId, noElement) ->
            val allEntriesForFormEntry = teamResultEntryRepository
                .findByPerformanceEntityIdAndFormEntryEntityId(performanceId, entryId)
            allEntriesForFormEntry.forEach { entry ->
                if (entry.noElement != noElement) {
                    entry.noElement = noElement
                    if (entry !in toSave) {
                        toSave += entry
                    }
                }
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
