package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.*
import odyseja.odysejapka.port.TimeTableUseCase
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/timeTable")
class TimeTableController(
  private val timeTableUseCase: TimeTableUseCase
) {

    @GetMapping()
    fun getAll(): List<Performance> {
      return timeTableUseCase.getAll()
    }

    @Secured("ROLE_ADMIN")
    @PostMapping()
    @ResponseBody
    fun addPerformance(@RequestBody performances: List<Performance>): List<PerformanceEntity> {
      return timeTableUseCase.addPerformance(performances)
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updatePerformance(@RequestBody performance: Performance) {
      timeTableUseCase.updatePerformance(performance)
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("{performanceId}")
    fun delPerformance(@PathVariable performanceId: Int) {
        timeTableUseCase.delPerformance(performanceId)
    }
}