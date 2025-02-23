package odyseja.odysejapka.gad

import GadRunner
import PunctationCells
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.ZspSheetsAdapter

internal class GadConfiguration(
    private val templatesFolderId: String,
    private val destinationFolderId: String,
    private val zspId: String,
    private val problemPunctuationCells: Map<String, PunctationCells>,
    private val driveAdapter: DriveAdapter
) {

    fun gadRunner(): GadRunner {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        return GadRunner(driveAdapter, sheetsAdapter, problemPunctuationCells, destinationFolderId, templatesFolderId)
    }
}