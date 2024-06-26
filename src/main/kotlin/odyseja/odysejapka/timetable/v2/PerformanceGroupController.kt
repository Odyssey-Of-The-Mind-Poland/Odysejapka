package odyseja.odysejapka.timetable.v2

import odyseja.odysejapka.timetable.PerformanceGroup
import odyseja.odysejapka.timetable.PerformanceGroupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/v2/timeTable")
class PerformanceGroupController(private val performanceGroupService: PerformanceGroupService) {

  @GetMapping
  fun getPerformanceGroups(@RequestParam(required = false) cityId: Int?): List<PerformanceGroup> {
    return performanceGroupService.getPerformanceGroups(cityId)
  }
}