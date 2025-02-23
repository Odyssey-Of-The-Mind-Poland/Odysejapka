package odyseja.odysejapka.fixer

import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter

class FixerConfiguration(
    private val folderId: String,
    private val pattern: String,
    private val cells: List<FixSheetsCommand.ReplacementCell>,
    private val driveAdapter: DriveAdapter
) {

    fun fixerRunner(): FixerRunner {
        val sheetsAdapter = SheetAdapter.getSheetAdapter()
        return FixerRunner(driveAdapter, sheetsAdapter, folderId, pattern, cells)
    }
}