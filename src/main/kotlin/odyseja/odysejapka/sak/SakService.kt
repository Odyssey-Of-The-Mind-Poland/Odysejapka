package odyseja.odysejapka.sak

import SakConfiguration
import odyseja.odysejapka.Progress
import odyseja.odysejapka.Status
import odyseja.odysejapka.async.ProcessRunner
import org.springframework.stereotype.Service

@Service
class SakService(
    private val sakCommandService: SakCommandService
) {

    private var runner: ProcessRunner? = null

    fun runGad(generateSakCommand: GenerateSakCommand, cityId: Int?) {
        sakCommandService.saveCommand(generateSakCommand, cityId)
        runner = ProcessRunner(
            SakConfiguration(
                generateSakCommand.templatesFolderId,
                generateSakCommand.zspId
            ).sakRunner()
        )
        runner?.start()
    }

    fun stop() {
        runner?.stop()
    }

    fun getProgress(): Progress {
        return runner?.getProgress() ?: Progress(0, Status.STOPPED, listOf())
    }
}