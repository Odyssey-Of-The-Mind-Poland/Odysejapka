package odyseja.odysejapka.gad

import odyseja.odysejapka.Progress
import odyseja.odysejapka.async.BackgroundJobService
import odyseja.odysejapka.util.GoogleIdExtractor
import org.springframework.stereotype.Service

@Service
class GadService(
    private val gadCommandService: GadCommandService,
    private val backgroundJobService: BackgroundJobService
) {

    private val jobType = "gad"

    fun runGad(generateGadCommand: GenerateGadCommand, cityId: Int?) {
        val command = generateGadCommand.copy(
            templatesFolderId = GoogleIdExtractor.extractGoogleId(generateGadCommand.templatesFolderId),
            destinationFolderId = GoogleIdExtractor.extractGoogleId(generateGadCommand.destinationFolderId),
            zspId = GoogleIdExtractor.extractGoogleId(generateGadCommand.zspId)
        )
        gadCommandService.saveCommand(command, cityId)
        backgroundJobService.start(
            jobType,
            GadConfiguration(
                command.templatesFolderId,
                command.destinationFolderId,
                command.zspId,
                command.problemPunctuationCells
            ).gadRunner()
        )
    }

    fun stop() {
        backgroundJobService.stop(jobType)
    }

    fun getProgress(): Progress {
        return backgroundJobService.getProgress(jobType)
    }
}