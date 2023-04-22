package odyseja.odysejapka.service

import odyseja.odysejapka.domain.PerformanceGroup
import org.springframework.stereotype.Service

@Service
class PerformanceGroupService(private val performanceRepository: PerformanceRepository) {
  fun getPerformanceGroups(): List<PerformanceGroup> {
    return performanceRepository.findAll().groupBy { it?.toGroup() }.map { (group, performances) ->
      PerformanceGroup(
        group = group!!,
        performances = performances.map { it?.toPerformance()!! }
      )
    }
  }

}
