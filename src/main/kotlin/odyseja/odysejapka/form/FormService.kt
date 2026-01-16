package odyseja.odysejapka.form

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(
    private val formProblemRepository: FormProblemRepository,
    private val cityFormJudgesRepository: CityFormJudgesRepository,
    private val teamFormService: TeamFormService,
    private val judgeCountService: JudgeCountService,
    private val teamResultService: TeamResultService,
) {

    @Transactional
    fun setFormEntries(problem: Int, form: ProblemForm) {
        val existingEntity = formProblemRepository.findByProblem(problem)
        val existingForm = existingEntity?.form

        val formWithIds = FormEntryMapHelper.assignIds(form, existingForm)

        val entity = existingEntity ?: FormProblemEntity().apply { this.problem = problem }
        entity.form = formWithIds
        formProblemRepository.save(entity)

        judgeCountService.setJudgesCount(problem, form.smallJudgesTeam, form.bigJudgesTeam)
    }


    fun getProblemForm(problem: Int): ProblemForm {
        val entity = formProblemRepository.findByProblem(problem)
        val form = entity?.form ?: ProblemForm(
            dtEntries = emptyList(),
            styleEntries = emptyList(),
            penaltyEntries = emptyList()
        )

        val judgeEntities = cityFormJudgesRepository.findByProblem(problem)
        val smallJudgesTeams = extractJudgeTeamIds(judgeEntities, 1)
        val bigJudgesTeams = extractJudgeTeamIds(judgeEntities, 2)

        return form.copy(
            smallJudgesTeam = smallJudgesTeams,
            bigJudgesTeam = bigJudgesTeams
        )
    }

    private fun extractJudgeTeamIds(judgeEntities: List<CityFormJudgesEntity>, judgeCount: Int): List<Int>? {
        val ids = judgeEntities
            .filter { it.judgeCount == judgeCount }
            .mapNotNull { it.city?.id }
        return ids.ifEmpty { null }
    }

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
