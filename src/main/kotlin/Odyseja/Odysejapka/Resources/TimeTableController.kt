package Odyseja.Odysejapka.Resources

import Odyseja.Odysejapka.data.model.Performance
import Odyseja.Odysejapka.repository.PerformanceRepository
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/timeTable")
class TimeTableController(
        private val timeTableRepostiry: PerformanceRepository
) {

    @GetMapping()
    fun getAll() : MutableIterable<Performance?> {
        return timeTableRepostiry.findAll()
    }
}