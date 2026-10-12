package odyseja.odysejapka.form

import odyseja.odysejapka.form.judge.team.JudgeTeamService
import odyseja.odysejapka.form.problem.form.FormData
import odyseja.odysejapka.form.problem.form.FormEntryMapHelper
import odyseja.odysejapka.form.problem.form.ProblemFormService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FormService(
    private val problemFormService: ProblemFormService,
    private val teamFormService: TeamFormService,
    private val judgeCountService: JudgeCountService,
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

        judgeCountService.setJudgesCount(problem, form.smallJudgesTeam, form.bigJudgesTeam)
    }

    @Transactional
    fun setFormDataV2(problem: Int, form: FormData) {
        val existingEntity = problemFormService.findByProblem(problem)
        val existingForm = existingEntity.form

        val formWithIds = FormEntryMapHelper.assignIds(form, existingForm)

        existingEntity.form = formWithIds

        val judgeTeams = form.judgeTeams ?: emptyList()

        judgeTeamService.setJudgeTeamsForProblem(problem, judgeTeams)
        problemFormService.saveProblemForm(existingEntity)
    }


    fun getFormData(problem: Int): FormData {
        val entity = problemFormService.findByProblem(problem)
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

    fun getFormDataV2(problem: Int): FormData {
        val entity = problemFormService.findByProblem(problem)
        val form = entity.form ?: FormData(
            dtEntries = emptyList(),
            styleEntries = emptyList(),
            penaltyEntries = emptyList(),
            judgeTeams = emptyList(),
        )

        return form.copy()
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

    fun getJudgeCountV2(problem: Int, cityId: Int): JudgeCount {
        return judgeTeamService.getJudgeCountByProblemAndCity(problem, cityId)
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

    @Transactional
    fun setTeamResultsV2(performanceId: Int, request: PerformanceResultsRequest) {
        teamResultService.setTeamResult(performanceId, request)
        val teamForm = teamFormService.getTeamFormV2(performanceId)
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

    fun getTeamFormV2(performanceId: Int): TeamForm {
        val teamForm = teamFormService.getTeamFormV2(performanceId)
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
    fun approveFormV2(performanceId: Int) {
        teamResultService.approveTeamResult(performanceId)

        val teamForm = teamFormService.getTeamFormV2(performanceId)
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
