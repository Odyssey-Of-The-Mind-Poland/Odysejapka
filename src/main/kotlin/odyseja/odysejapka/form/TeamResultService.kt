package odyseja.odysejapka.form

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamResultService(
    private val teamResultRepository: TeamResultRepository,
) {

    @Transactional
    fun setTeamResults(performanceId: Int, request: PerformanceResultsRequest) {
        val entity = teamResultRepository.findByPerformanceId(performanceId)
            ?: TeamResultEntity().apply { this.performanceId = performanceId }
        
        entity.results = request
        teamResultRepository.save(entity)
    }

}

