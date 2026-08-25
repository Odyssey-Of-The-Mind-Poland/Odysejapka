package odyseja.odysejapka.timetable

import org.springframework.stereotype.Service

@Service
class PerformanceGroupService(
    private val performanceService: PerformanceService
) {
    fun getPerformanceGroups(cityId: Int?): List<PerformanceGroup> {
        val performances =
            cityId?.let { performanceService.getPerformanceEntitiesByCity(cityId) } ?: performanceService.getAllPerformanceEntities()
        return performances
            .groupBy { it?.toGroup() }.map { (group, performances) ->
                PerformanceGroup(
                    group = group!!,
                    performances = performances.map { it?.toPerformance()!! }
                )
            }
    }
}
