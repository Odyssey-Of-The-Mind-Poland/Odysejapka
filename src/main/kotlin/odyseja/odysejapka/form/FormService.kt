package odyseja.odysejapka.form

import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(
    private val formEntryRepository: FormEntryRepository,
    private val formProblemRepository: FormProblemRepository,
    private val teamResultEntryRepository: TeamResultEntryRepository,
    private val performanceRepository: PerformanceRepository
) {

    @Transactional
    fun setFormEntries(problem: Int, formEntries: List<FormEntry>) {
        val existing = formEntryRepository.findByProblem(problem)
        val byId = existing.associateBy { it.id }

        val requestedIds: Set<Long> = formEntries.mapNotNull { it.id }.toSet()

        val toPersist = mutableListOf<FormEntryEntity>()

        for (dto in formEntries) {
            val entity = when (val id = dto.id) {
                null -> {
                    FormEntryEntity().apply { this.problem = problem }
                }
                else -> {
                    val found = byId[id]
                    if (found != null) {
                        found.problem = problem
                        found
                    } else {
                        FormEntryEntity().apply { this.problem = problem }
                    }
                }
            }

            entity.name = dto.name
            entity.calcType = dto.calcType

            toPersist += entity
        }

        val toDelete = existing.filter { it.id !in requestedIds }
        if (toDelete.isNotEmpty()) {
            formEntryRepository.deleteAll(toDelete)
        }

        formEntryRepository.saveAll(toPersist)
    }

    fun getFormEntries(problem: Int): List<FormEntry> {
        return formEntryRepository.findByProblem(problem).map { it.toFormEntry() }
    }

    @Transactional
    fun setJudgeCount(problem: Int, count: Int): Int {
        val problemEntity = formProblemRepository.findByProblem(problem) ?: FormProblemEntity().apply {
            this.problem = problem
        }
        problemEntity.judgeCount = count
        formProblemRepository.save(problemEntity)
        return count
    }

    fun getJudgeCount(problem: Int): Int {
        return formProblemRepository.findByProblem(problem)?.judgeCount ?: 0
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

    fun getTeamResults(performanceId: Int): List<FormResult> {
        return teamResultEntryRepository.findByPerformanceEntityId(performanceId).map {
            FormResult(
                performanceId = it.performanceEntity?.id ?: 0,
                entryId = it.formEntryEntity?.id ?: 0,
                judge = it.judge,
                result = it.result
            )
        }
    }
}
