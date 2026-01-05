package odyseja.odysejapka.sak

import odyseja.odysejapka.Progress
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sak")
class SakController(
    private val sakService: SakService,
    private val sakCommandService: SakCommandService
) {

    @PostMapping
    @Secured("ROLE_ADMINISTRATOR")
    fun startGad(
        @RequestBody generateGadCommand: GenerateSakCommand,
        @RequestParam(required = false) cityId: Int?
    ) {
        sakService.runGad(generateGadCommand, cityId)
    }

    @PostMapping("/stop")
    @Secured("ROLE_ADMINISTRATOR")
    fun stopGad() {
        sakService.stop()
    }

    @GetMapping("/status")
    fun getGadStatus(): Progress {
        return sakService.getProgress()
    }

    @GetMapping
    fun getSak(@RequestParam(required = false) cityId: Int?): GenerateSakCommand {
        return sakCommandService.getCommand(cityId)
    }
}