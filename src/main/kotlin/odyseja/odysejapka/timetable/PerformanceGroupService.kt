package odyseja.odysejapka.timetable

import org.springframework.stereotype.Service

@Service
class PerformanceGroupService(
    private val timeTableService: TimeTableService
) {
    fun getPerformanceGroups(cityId: Int?): List<PerformanceGroup> {
        val performances =
            cityId?.let { timeTableService.getPerformanceEntitiesByCity(cityId) } ?: timeTableService.getAllPerformanceEntities()
        return performances
            .filter { it != null && !it.isExcludedFromScoring() }
            .groupBy { it?.toGroup() }.map { (group, performances) ->
                PerformanceGroup(
                    group = group!!,
                    performances = performances.map { it?.toPerformance()!! }
                )
            }
    }
}
