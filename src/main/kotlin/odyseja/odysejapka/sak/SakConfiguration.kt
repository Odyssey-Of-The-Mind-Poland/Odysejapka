import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.sak.SakRunner
import odyseja.odysejapka.timetable.TimeTableService

internal class SakConfiguration(
    private val templatesFolderId: String,
    private val zspId: String,
    private val driveAdapter: DriveAdapter
) {

    fun sakRunner(): SakRunner {
        val sheetsAdapter = SheetAdapter.getSheetAdapter()
        return SakRunner(driveAdapter, sheetsAdapter, zspId, templatesFolderId)
    }
}