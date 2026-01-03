package odyseja.odysejapka.form

import odyseja.odysejapka.city.CityRepository
import odyseja.odysejapka.form.FormEntryEntity.Companion.from
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

        fun toEntity(
            dto: FormEntry,
            category: FormEntryEntity.FormCategory
        ): FormEntryEntity {
            val entity = dto.id?.let { existingById[it] }
            return if (entity != null) {
                entity.apply {
                    name = dto.name
                    calcType = dto.calcType
                    formCategory = category
                }
            } else {
                from(problem, dto, category)
            }
        }

        fun purgeMissing(dtos: List<FormEntry>, category: FormEntryEntity.FormCategory) {
            val requestedIds = dtos.asSequence().mapNotNull { it.id }.toSet()
            val toDelete = existingByCategory[category].orEmpty()
                .filter { it.id !in requestedIds }

            if (toDelete.isNotEmpty()) {
                toDelete.forEach { teamResultEntryRepository.deleteAllByFormEntryEntity(it) }
                formEntryRepository.deleteAll(toDelete)
            }
        }

        val categories = listOf(
            FormEntryEntity.FormCategory.DT to form.dtEntries,
            FormEntryEntity.FormCategory.STYLE to form.styleEntries,
            FormEntryEntity.FormCategory.PENALTY to form.penaltyEntries
        )

        categories.forEach { (cat, dtos) -> purgeMissing(dtos, cat) }

        val toPersist = categories
            .flatMap { (cat, dtos) -> dtos.map { dto -> toEntity(dto, cat) } }

        formEntryRepository.saveAll(toPersist)
    }


    fun getFormEntries(problem: Int): ProblemForm {
        val entries = formEntryRepository.findByProblem(problem)
        return ProblemForm(
            entries.filter { it.formCategory == FormEntryEntity.FormCategory.DT }
                .map { it.toFormEntry() },
            entries.filter { it.formCategory == FormEntryEntity.FormCategory.STYLE }
                .map { it.toFormEntry() },
            entries.filter { it.formCategory == FormEntryEntity.FormCategory.PENALTY }
                .map { it.toFormEntry() })
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
