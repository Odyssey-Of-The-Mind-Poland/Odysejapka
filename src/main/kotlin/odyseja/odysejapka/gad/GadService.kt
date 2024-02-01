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
        start(generateGadCommand)
    }

    fun stop() {
        job?.stop()
        gad = null
    }

    private fun start(generateGadCommand: GenerateGadCommand) {
        if (gad != null || job?.isAlive == true) {
            throw RuntimeException("Gad is already running")
        }
        gad = GadConfiguration(
            generateGadCommand.templatesFolderId,
            generateGadCommand.destinationFolderId,
            generateGadCommand.zspId,
            generateGadCommand.problemPunctuationCells
        ).gadRunner()
        job = Thread {
            gad?.createForms()
        }
        job?.start()
    }

    fun getProgress(): GadProgress {
        val progress = gad?.getProgress() ?: 100
        return if (progress != 100) {
            GadProgress(progress, GadStatus.RUNNING)
        } else {
            GadProgress(100, GadStatus.STOPPED)
        }
    }
}