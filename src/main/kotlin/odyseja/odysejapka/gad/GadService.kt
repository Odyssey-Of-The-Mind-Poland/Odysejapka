package odyseja.odysejapka.gad

import GadConfiguration
import odyseja.odysejapka.Progress
import odyseja.odysejapka.Status
import odyseja.odysejapka.async.ProcessRunner
import org.springframework.stereotype.Service

@Service
class GadService(
    private val gadCommandService: GadCommandService
) {

    private var runner: ProcessRunner? = null

    fun runGad(generateGadCommand: GenerateGadCommand) {
        gadCommandService.saveCommand(generateGadCommand)
        runner = ProcessRunner(
            GadConfiguration(
                generateGadCommand.templatesFolderId,
                generateGadCommand.destinationFolderId,
                generateGadCommand.zspId,
                generateGadCommand.problemPunctuationCells
            ).gadRunner()
        )
        runner?.start()
    }

    fun stop() {
        runner?.stop()
    }

    fun getProgress(): Progress {
        return runner?.getProgress() ?: Progress(0, Status.STOPPED)
    }
}