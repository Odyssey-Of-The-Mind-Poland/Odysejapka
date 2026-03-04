package odyseja.odysejapka.sak

import SakConfiguration
import odyseja.odysejapka.Progress
import odyseja.odysejapka.async.BackgroundJobService
import odyseja.odysejapka.util.GoogleIdExtractor
import org.springframework.stereotype.Service

@Service
class SakService(
    private val sakCommandService: SakCommandService,
    private val backgroundJobService: BackgroundJobService
) {

    private val jobType = "sak"

    fun runGad(generateSakCommand: GenerateSakCommand, cityId: Int?) {
        val command = generateSakCommand.copy(
            templatesFolderId = GoogleIdExtractor.extractGoogleId(generateSakCommand.templatesFolderId),
            zspId = GoogleIdExtractor.extractGoogleId(generateSakCommand.zspId)
        )
        sakCommandService.saveCommand(command, cityId)
        backgroundJobService.start(
            jobType,
            SakConfiguration(
                command.templatesFolderId,
                command.zspId
            ).sakRunner()
        )
    }

    fun stop() {
        backgroundJobService.stop(jobType)
    }

    fun getProgress(): Progress {
        return backgroundJobService.getProgress(jobType)
    }
}