import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.sak.SakRunner

internal class SakConfiguration(
    private val templatesFolderId: String,
    private val zspId: String,
    private val driveAdapter: DriveAdapter,
    private val sheetAdapter: SheetAdapter
) {

    fun sakRunner(): SakRunner {
        return SakRunner(driveAdapter, sheetAdapter, zspId, templatesFolderId, ZspSheetsAdapter(sheetAdapter, zspId))
    }
}