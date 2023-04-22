package odyseja.odysejapka.rest.v2

import odyseja.odysejapka.domain.PerformanceGroup
import odyseja.odysejapka.service.PerformanceGroupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/v2/timeTable")
class PerformanceGroupController(private val performanceGroupService: PerformanceGroupService) {

  @GetMapping
  fun getPerformanceGroups(): List<PerformanceGroup> {
    return performanceGroupService.getPerformanceGroups()
  }
}