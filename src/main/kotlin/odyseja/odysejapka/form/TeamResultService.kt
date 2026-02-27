package odyseja.odysejapka.form

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class TeamResultService(
    private val teamResultRepository: TeamResultRepository,
) {

    @Transactional
    fun setTeamResults(performanceId: Int, request: PerformanceResultsRequest) {
        val entity = teamResultRepository.findByPerformanceId(performanceId)
            ?: TeamResultEntity().apply { this.performanceId = performanceId }

        entity.results = request
        entity.approved = false
        entity.performanceAt = request.performanceAt.ifBlank { null }
        entity.performanceTime = request.performanceTime.ifBlank { null }
        teamResultRepository.save(entity)
    }

    @Transactional
    fun approveTeamResult(performanceId: Int) {
        val entity = teamResultRepository.findByPerformanceId(performanceId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        entity.approved = true
        entity.formState = FormState.APPROVED
        teamResultRepository.save(entity)
    }

    @Transactional
    fun updateFormState(performanceId: Int, state: FormState) {
        val entity = teamResultRepository.findByPerformanceId(performanceId) ?: return
        entity.formState = state
        teamResultRepository.save(entity)
    }

    @Transactional
    fun updateRawScores(performanceId: Int, rawDt: Double, rawStyle: Double, rawPenalty: Double, rawWeight: Double?, rawTotal: Double) {
        val entity = teamResultRepository.findByPerformanceId(performanceId) ?: return
        entity.rawDt = rawDt
        entity.rawStyle = rawStyle
        entity.rawPenalty = rawPenalty
        entity.rawWeight = rawWeight
        entity.rawTotal = rawTotal
        teamResultRepository.save(entity)
    }
}
