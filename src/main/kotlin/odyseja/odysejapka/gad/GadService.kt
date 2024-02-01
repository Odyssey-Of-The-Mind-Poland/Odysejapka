package odyseja.odysejapka.gad

import GadConfiguration
import GadRunner
import org.springframework.stereotype.Service

@Service
class GadService(
    private val gadCommandService: GadCommandService
) {

    private var gad: GadRunner? = null
    private var job: Thread? = null

    fun runGad(generateGadCommand: GenerateGadCommand) {
        gadCommandService.saveCommand(generateGadCommand)
        gad = GadConfiguration(
            generateGadCommand.templatesFolderId,
            generateGadCommand.destinationFolderId,
            generateGadCommand.zspId,
            generateGadCommand.problemPunctuationCells
        ).gadRunner()
        start()
    }

    fun stop() {
        job?.stop()
    }

    private fun start() {
        job = Thread {
            gad?.createForms()
        }
        job?.start()
    }
}