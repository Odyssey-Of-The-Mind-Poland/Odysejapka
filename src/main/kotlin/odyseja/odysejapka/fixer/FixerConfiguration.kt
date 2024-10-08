package odyseja.odysejapka.fixer

import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter

class FixerConfiguration(
    private val folderId: String,
    private val pattern: String,
    private val cells: List<FixSheetsCommand.ReplacementCell>
) {

    fun fixerRunner(): FixerRunner {
        val driveAdapter = DriveAdapter.getDriveAdapter()
        val sheetsAdapter = SheetAdapter.getSheetAdapter()
        return FixerRunner(driveAdapter, sheetsAdapter, folderId, pattern, cells)
    }
}