package odyseja.odysejapka.fixer

import odyseja.odysejapka.async.ProcessRunner
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import org.springframework.stereotype.Service

@Service
class FixerService(
    private val driveAdapter: DriveAdapter,
    private val sheetAdapter: SheetAdapter
) {

    private var runner: ProcessRunner? = null

    fun start(fixSheetsCommand: FixSheetsCommand) {
        runner = ProcessRunner(
            FixerConfiguration(
                fixSheetsCommand.folderId,
                fixSheetsCommand.pattern,
                fixSheetsCommand.cells,
                driveAdapter,
                sheetAdapter
            ).fixerRunner()
        )
        runner?.start()
    }

    fun stop() {
        runner?.stop()
    }

    fun createFixerRunner(folderId: String, pattern: String, cells: List<FixSheetsCommand.ReplacementCell>): FixerRunner {
        return FixerConfiguration(folderId, pattern, cells, driveAdapter, sheetAdapter).fixerRunner()
    }
}