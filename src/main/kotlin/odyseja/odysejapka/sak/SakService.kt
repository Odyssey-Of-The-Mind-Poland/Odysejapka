package odyseja.odysejapka.sak

import SakConfiguration
import odyseja.odysejapka.Progress
import odyseja.odysejapka.Status
import odyseja.odysejapka.async.ProcessRunner
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.timetable.TimeTableService
import org.springframework.stereotype.Service

@Service
class SakService(val driveAdapter: DriveAdapter, val sheetAdapter: SheetAdapter) {

    private var runner: ProcessRunner? = null

    fun runGad(generateSakCommand: GenerateSakCommand) {
        runner = ProcessRunner(
            SakConfiguration(
                generateSakCommand.templatesFolderId,
                generateSakCommand.zspId,
                driveAdapter,
                sheetAdapter
            ).sakRunner()
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