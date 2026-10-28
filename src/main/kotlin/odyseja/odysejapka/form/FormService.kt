package odyseja.odysejapka.form

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(
    private val problemFormService: ProblemFormService,
    private val teamFormService: TeamFormService,
    private val teamResultService: TeamResultService,
    private val formValidationService: FormValidationService,
    private val formAnomalyService: FormAnomalyService,
    private val judgeTeamService: JudgeTeamService,
) {

    @Transactional
    fun setFormData(problem: Int, form: FormData) {
        val existingEntity = problemFormService.findByProblem(problem)
        val existingForm = existingEntity.form

        val formWithIds = FormEntryMapHelper.assignIds(form, existingForm)
        existingEntity.form = formWithIds
        problemFormService.saveProblemForm(existingEntity)
    }

    fun getFormData(problem: Int): FormData {
        val entity = problemFormService.findByProblem(problem)
        val form = entity.form ?: FormData(
            dtEntries = emptyList(),
            styleEntries = emptyList(),
            penaltyEntries = emptyList()
        )

        return form
    }

    @Transactional
    fun setTeamResults(performanceId: Int, request: PerformanceResultsRequest) {
        teamResultService.setTeamResult(performanceId, request)
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
