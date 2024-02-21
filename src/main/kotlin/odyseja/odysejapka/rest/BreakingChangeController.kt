package odyseja.odysejapka.rest

import odyseja.odysejapka.service.BreakingChangeService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/breaking/change")
class BreakingChangeController(private val breakingChangeService: BreakingChangeService) {

    @GetMapping
    fun getLastBrakingChange(): BreakingChange {
        return breakingChangeService.getLastBreakingChange()
    }

    @PutMapping
    @Secured("ROLE_ADMIN")
    fun setBreakingChange(@RequestBody breakingChange: BreakingChange) {
        breakingChangeService.setBreakingChange(breakingChange)
    }

    @GetMapping("/shouldUpdate")
    fun shouldUpdate(@RequestParam version: String): Boolean {
        return breakingChangeService.shouldUpdate(version)
    }

    data class BreakingChange(
        val version: String
    )
}