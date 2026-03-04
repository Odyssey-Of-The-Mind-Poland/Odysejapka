package odyseja.odysejapka.fixer

import odyseja.odysejapka.Progress
import odyseja.odysejapka.async.BackgroundJobService
import odyseja.odysejapka.util.GoogleIdExtractor
import org.springframework.stereotype.Service

@Service
class FixerService(
    private val backgroundJobService: BackgroundJobService
) {

    private val jobType = "fixer"

    fun start(fixSheetsCommand: FixSheetsCommand) {
        val folderId = GoogleIdExtractor.extractGoogleId(fixSheetsCommand.folderId)
        backgroundJobService.start(
            jobType,
            FixerConfiguration(
                folderId,
                fixSheetsCommand.pattern,
                fixSheetsCommand.cells
            ).fixerRunner()
        )
    }

    fun stop() {
        backgroundJobService.stop(jobType)
    }

    fun getProgress(): Progress {
        return backgroundJobService.getProgress(jobType)
    }
}