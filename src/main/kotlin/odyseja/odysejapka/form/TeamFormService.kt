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
        return TeamForm(performanceId, getResults(template, results))
    }

    fun getResults(
        templateEntries: List<FormEntryEntity>,
        resultEntries: List<TeamResultEntryEntity>
    ): List<TeamForm.FormEntryRow> {
        return templateEntries.map { templateEntry ->
            val judgeResults = resultEntries
                .filter { it.formEntryEntity?.id == templateEntry.id }
                .groupBy { it.judge }
                .mapValues { (_, entries) -> entries.sumOf { it.result } }

            TeamForm.FormEntryRow(
                entryId = templateEntry.id,
                judgeResults = judgeResults
            )
        }
    }
}