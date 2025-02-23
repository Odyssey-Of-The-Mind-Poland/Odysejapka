package odyseja.odysejapka.gad

import odyseja.odysejapka.Progress
import odyseja.odysejapka.Status
import odyseja.odysejapka.async.ProcessRunner
import odyseja.odysejapka.drive.DriveAdapter
import org.springframework.stereotype.Service

@Service
class GadService(
    private val gadCommandService: GadCommandService,
    private val driveAdapter: DriveAdapter
) {

    private var runner: ProcessRunner? = null

    fun runGad(generateGadCommand: GenerateGadCommand) {
        gadCommandService.saveCommand(generateGadCommand)
        runner = ProcessRunner(
            GadConfiguration(
                generateGadCommand.templatesFolderId,
                generateGadCommand.destinationFolderId,
                generateGadCommand.zspId,
                generateGadCommand.problemPunctuationCells,
                driveAdapter
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