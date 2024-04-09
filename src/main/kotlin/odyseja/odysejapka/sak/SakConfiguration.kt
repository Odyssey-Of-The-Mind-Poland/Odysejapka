import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.sak.SakRunner
import odyseja.odysejapka.timetable.TimeTableService

internal class SakConfiguration(
    private val templatesFolderId: String,
    private val timetableService: TimeTableService,
    private val zspId: String
) {

    fun sakRunner(): SakRunner {
        val driveAdapter = DriveAdapter.getDriveAdapter()
        val sheetsAdapter = SheetAdapter.getSheetAdapter()
        return SakRunner(driveAdapter, sheetsAdapter, timetableService, zspId, templatesFolderId)
    }
}