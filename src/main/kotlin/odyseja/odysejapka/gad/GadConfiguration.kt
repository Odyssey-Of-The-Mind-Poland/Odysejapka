import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.ZspSheetsAdapter

internal class GadConfiguration(
    val templatesFolderId: String,
    val destinationFolderId: String,
    val zspId: String,
    val problemPunctuationCells: Map<String, PunctationCells>
) {

    fun gadRunner(): GadRunner {
        val driveAdapter = DriveAdapter.getDriveAdapter()
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        return GadRunner(driveAdapter, sheetsAdapter, problemPunctuationCells, destinationFolderId, templatesFolderId)
    }
}