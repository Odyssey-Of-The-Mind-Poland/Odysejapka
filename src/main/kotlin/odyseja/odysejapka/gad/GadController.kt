package odyseja.odysejapka.gad

import GadConfiguration
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/gad")
class GadController(private val gadCommandService: GadCommandService) {

    @PostMapping
    @Secured("ROLE_ADMIN")
    fun startGad(@RequestBody generateGadCommand: GenerateGadCommand) {
        gadCommandService.saveCommand(generateGadCommand)
        val gad = GadConfiguration(
            generateGadCommand.templatesFolderId,
            generateGadCommand.destinationFolderId,
            generateGadCommand.zspId,
            generateGadCommand.problemPunctuationCells
        ).gadService()
        gad.createForms()
    }

    @GetMapping
    fun getGad(): GenerateGadCommand {
        return gadCommandService.getCommand()
    }
}