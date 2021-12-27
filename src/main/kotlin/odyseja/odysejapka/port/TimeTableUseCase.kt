package odyseja.odysejapka.port

import odyseja.odysejapka.domain.Performance
import odyseja.odysejapka.domain.PerformanceEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

interface TimeTableUseCase {

  fun getAll(): List<Performance>

  fun addPerformance(@RequestBody performances: List<Performance>): List<PerformanceEntity>

  fun updatePerformance(@RequestBody performance: Performance)

  fun delPerformance(@PathVariable id: Int)
}