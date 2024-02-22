package odyseja.odysejapka.timetable

import odyseja.odysejapka.domain.PerformanceGroup
import org.springframework.stereotype.Service

@Service
class PerformanceGroupService(private val performanceRepository: PerformanceRepository) {
    fun getPerformanceGroups(cityId: Int?): List<PerformanceGroup> {
        val performances =
            cityId?.let { performanceRepository.findAllByCityEntity_Id(cityId) } ?: performanceRepository.findAll()
        return performances.groupBy { it?.toGroup() }.map { (group, performances) ->
            PerformanceGroup(
                group = group!!,
                performances = performances.map { it?.toPerformance()!! }
            )
        }
    }

}
