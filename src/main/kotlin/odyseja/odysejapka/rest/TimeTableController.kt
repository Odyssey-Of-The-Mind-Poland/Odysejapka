package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.*
import odyseja.odysejapka.service.TimeTableService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/timeTable")
class TimeTableController(
  private val timeTableService: TimeTableService
) {

    @GetMapping()
    fun getAll(): List<Performance> {
      return timeTableService.getAll()
    }

    @Secured("ROLE_ADMIN")
    @PostMapping()
    @ResponseBody
    fun addPerformance(@RequestBody performances: List<Performance>): List<PerformanceEntity> {
      return timeTableService.addPerformance(performances)
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updatePerformance(@RequestBody performance: Performance) {
        timeTableService.updatePerformance(performance)
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("{performanceId}")
    fun delPerformance(@PathVariable performanceId: Int) {
        timeTableService.delPerformance(performanceId)
    }
}