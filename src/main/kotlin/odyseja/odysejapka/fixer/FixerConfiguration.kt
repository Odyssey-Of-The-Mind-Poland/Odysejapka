package odyseja.odysejapka.fixer

import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter

class FixerConfiguration(
    private val folderId: String,
    private val pattern: String,
    private val cells: List<FixSheetsCommand.ReplacementCell>,
    private val driveAdapter: DriveAdapter,
    private val sheetAdapter: SheetAdapter
) {

    fun fixerRunner(): FixerRunner {
        return FixerRunner(driveAdapter, sheetAdapter, folderId, pattern, cells)
    }
}