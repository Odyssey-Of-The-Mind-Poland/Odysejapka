package odyseja.odysejapka.planner

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/plan")
class PlannerController(private val plannerService: PlannerService) {

    @PostMapping
    fun plan(): TimeTable? {
        return plannerService.plan()
    }
}