package odyseja.odysejapka.form

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(
    private val formProblemService: FormProblemService,
    private val teamFormService: TeamFormService,
    private val judgeCountService: JudgeCountService,
    private val teamResultService: TeamResultService,
    private val formValidationService: FormValidationService,
    private val formAnomalyService: FormAnomalyService,
) {

    @Transactional
    fun setFormEntries(problem: Int, form: FormData) {
        val existingEntity = formProblemService.findByProblem(problem)
        val existingForm = existingEntity.form

        val formWithIds = FormEntryMapHelper.assignIds(form, existingForm)

        existingEntity.form = formWithIds
        formProblemService.saveFormProblem(existingEntity)

        judgeCountService.setJudgesCount(problem, form.smallJudgesTeam, form.bigJudgesTeam)
    }


    fun getFormData(problem: Int): FormData {
        val entity = formProblemService.findByProblem(problem)
        val form = entity.form ?: FormData(
            dtEntries = emptyList(),
            styleEntries = emptyList(),
            penaltyEntries = emptyList()
        )

        val judgeEntities = judgeCountService.getJudgeCountByProblem(problem)
        val smallJudgesTeams = extractJudgeTeamIds(judgeEntities, 1)
        val bigJudgesTeams = extractJudgeTeamIds(judgeEntities, 2)

        return form.copy(
            smallJudgesTeam = smallJudgesTeams,
            bigJudgesTeam = bigJudgesTeams
        )
    }

    private fun extractJudgeTeamIds(judgeEntities: List<CityFormJudgesEntity?>, judgeCount: Int): List<Int>? {
        val ids = judgeEntities
            .filter { it?.judgeCount == judgeCount }
            .mapNotNull { it?.city?.id }
        return ids.ifEmpty { null }
    }

    fun getJudgeCount(problem: Int, cityId: Int): JudgeCountResponse {
        return judgeCountService.getJudgeCountByProblemAndCity(problem, cityId)
    }

    @Transactional
    fun setTeamResults(performanceId: Int, request: PerformanceResultsRequest) {
        teamResultService.setTeamResults(performanceId, request)
        val teamForm = teamFormService.getTeamForm(performanceId)
        val errors = formValidationService.validateTeamForm(teamForm)
        val anomalies = formAnomalyService.detectAnomalies(teamForm)
        val state = when {
            errors.isNotEmpty() -> FormState.ERROR
            anomalies.isNotEmpty() -> FormState.ANOMALY
            else -> FormState.SCORED
        }
        teamResultService.updateFormState(performanceId, state)
    }

    fun getTeamForm(performanceId: Int): TeamForm {
        val teamForm = teamFormService.getTeamForm(performanceId)
        val errors = formValidationService.validateTeamForm(teamForm)
        val anomalies = formAnomalyService.detectAnomalies(teamForm)
        val canPreview = errors.isEmpty()
        return teamForm.copy(validationErrors = errors, anomalies = anomalies, canPreview = canPreview)
    }

    @Transactional
    fun approveForm(performanceId: Int) {
        teamResultService.approveTeamResult(performanceId)

        val teamForm = teamFormService.getTeamForm(performanceId)
        val rawForm = TeamFormToRawTeamFormConverter.convert(teamForm)
        val rawWeight = if (teamForm.problem == 4) {
            teamForm.weightHeldEntries.flatMap { it.weights }.sum()
        } else null
        teamResultService.updateRawScores(
            performanceId = performanceId,
            rawDt = rawForm.dtSum,
            rawStyle = rawForm.styleSum,
            rawPenalty = rawForm.penaltySum,
            rawWeight = rawWeight,
            rawTotal = rawForm.totalSum
        )
    }

    @Transactional
    fun toggleRanatra(performanceId: Int): Boolean {
        return teamResultService.toggleRanatra(performanceId)
    }
}
