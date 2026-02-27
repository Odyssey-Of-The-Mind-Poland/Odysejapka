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
        teamResultRepository.save(entity)
    }

    @Transactional
    fun approveTeamResult(performanceId: Int) {
        val entity = teamResultRepository.findByPerformanceId(performanceId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        entity.approved = true
        teamResultRepository.save(entity)
    }
}
