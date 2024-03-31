package odyseja.odysejapka.sak

import odyseja.odysejapka.Progress
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sak")
class SakController(private val sakService: SakService) {

    @PostMapping
    @Secured("ROLE_ADMIN")
    fun startGad(@RequestBody generateGadCommand: GenerateSakCommand) {
        sakService.runGad(generateGadCommand)
    }

    @PostMapping("/stop")
    @Secured("ROLE_ADMIN")
    fun stopGad() {
        sakService.stop()
    }

    @GetMapping("/status")
    fun getGadStatus(): Progress {
        return sakService.getProgress()
    }
}