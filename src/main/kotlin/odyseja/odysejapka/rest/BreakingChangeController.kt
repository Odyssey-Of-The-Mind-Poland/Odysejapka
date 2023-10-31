package odyseja.odysejapka.rest

import odyseja.odysejapka.service.BreakingChangeService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/breaking/change")
class BreakingChangeController(private val breakingChangeService: BreakingChangeService) {

    @GetMapping
    @Secured("ROLE_ADMIN")
    fun getLastBrakingChange(): BreakingChange {
        return breakingChangeService.getLastBreakingChange()
    }

    @PutMapping
    fun setBreakingChange(@RequestBody breakingChange: BreakingChange) {
        breakingChangeService.setBreakingChange(breakingChange)
    }

    data class BreakingChange(
        val version: String
    )
}