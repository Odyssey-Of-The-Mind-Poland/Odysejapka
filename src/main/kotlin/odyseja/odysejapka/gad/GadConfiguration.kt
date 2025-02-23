package odyseja.odysejapka.gad

import GadRunner
import PunctationCells
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.drive.ZspSheetsAdapter

internal class GadConfiguration(
    private val templatesFolderId: String,
    private val destinationFolderId: String,
    private val zspId: String,
    private val problemPunctuationCells: Map<String, PunctationCells>,
    private val driveAdapter: DriveAdapter,
    private val sheetAdapter: SheetAdapter
) {

    fun gadRunner(): GadRunner {
        val zspSheetsAdapter = ZspSheetsAdapter(sheetAdapter, zspId)
        return GadRunner(driveAdapter, zspSheetsAdapter, problemPunctuationCells, destinationFolderId, templatesFolderId)
    }
}