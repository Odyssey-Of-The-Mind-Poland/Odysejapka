package odyseja.odysejapka.gad

import odyseja.odysejapka.Progress
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/gad")
class GadController(private val gadCommandService: GadCommandService, private val gadService: GadService) {

    @PostMapping
    @Secured("ROLE_ADMIN")
    fun startGad(@RequestBody generateGadCommand: GenerateGadCommand) {
        gadService.runGad(generateGadCommand)
    }

    @PostMapping("/stop")
    @Secured("ROLE_ADMIN")
    fun stopGad() {
        gadService.stop()
    }

    @GetMapping("/status")
    fun getGadStatus(): Progress {
        return gadService.getProgress()
    }

    @GetMapping
    fun getGad(): GenerateGadCommand {
        return gadCommandService.getCommand()
    }
}