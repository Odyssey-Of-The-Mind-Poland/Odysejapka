package odyseja.odysejapka.form

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamResultService(
    private val teamResultRepository: TeamResultRepository,
) {

    fun getTeamResult(performanceId: Int): TeamResultEntity {
        return teamResultRepository.findByPerformanceId(performanceId)
            ?: throw EntityNotFoundException(
                "Nie ma wyników dla przedstawienia o ID $performanceId lub takie przedstawienie nie istnieje"
            )
    }

    fun getTeamResults(performanceIds: List<Int>): List<TeamResultEntity> {
        return teamResultRepository.findAllByPerformanceIdIn(performanceIds)
    }

    @Transactional
    fun setTeamResults(performanceId: Int, request: PerformanceResultsRequest) {
        val entity = try {
            getTeamResult(performanceId)
        } catch (_: EntityNotFoundException) {
            TeamResultEntity().apply { this.performanceId = performanceId }
        }

        entity.results = request
        entity.approved = false
        entity.performanceAt = request.performanceAt.ifBlank { null }
        entity.performanceTime = request.performanceTime.ifBlank { null }
        teamResultRepository.save(entity)
    }

    @Transactional
    fun approveTeamResult(performanceId: Int) {
        val entity = getTeamResult(performanceId)
        entity.approved = true
        entity.formState = FormState.APPROVED
        teamResultRepository.save(entity)
    }

    @Transactional
    fun updateFormState(performanceId: Int, state: FormState) {
        val entity = getTeamResult(performanceId)
        entity.formState = state
        teamResultRepository.save(entity)
    }

    @Transactional
    fun updateRawScores(performanceId: Int, rawDt: Double, rawStyle: Double, rawPenalty: Double, rawWeight: Double?, rawTotal: Double) {
        val entity = getTeamResult(performanceId)
        entity.rawDt = rawDt
        entity.rawStyle = rawStyle
        entity.rawPenalty = rawPenalty
        entity.rawWeight = rawWeight
        entity.rawTotal = rawTotal
        teamResultRepository.save(entity)
    }

    @Transactional
    fun toggleRanatra(performanceId: Int): Boolean {
        val entity = try {
            getTeamResult(performanceId)
        } catch (_: EntityNotFoundException) {
            TeamResultEntity().apply { this.performanceId = performanceId }
        }
        entity.ranatra = !entity.ranatra
        teamResultRepository.save(entity)
        return entity.ranatra
    }
}
