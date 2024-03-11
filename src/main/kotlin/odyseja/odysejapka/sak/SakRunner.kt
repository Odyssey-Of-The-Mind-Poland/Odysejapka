package odyseja.odysejapka.sak

import com.google.api.services.drive.model.File
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.drive.SpontanGroups
import odyseja.odysejapka.timetable.TimeTableService
import java.util.concurrent.atomic.AtomicInteger

internal class SakRunner(
    private val driveAdapter: DriveAdapter,
    private val sheetsAdapter: SheetAdapter,
    private val timetableService: TimeTableService
) {

    private val templates = getTemplates()
    private var totalSheetCount = 0
    private var processedSheetCount = AtomicInteger(0)

    fun startSak() {
        val teams = timetableService.getAll()
        val groups = teams.groupBy { SpontanGroups.Group.fromPerformance(it) }.map { (group, performances) ->
            SpontanGroups(group, performances)
        }
        for (group in groups) {
            processGroup(group)
        }
    }

    private fun processGroup(group: SpontanGroups) {
        val sheetFile = templates.filter { it.originalFilename.startsWith(group.groupCode()) }[0]
        val sheetName = sheetsAdapter.getSheet(sheetFile.id)!!.get(0).properties.title
        val values = sheetsAdapter.getValue(sheetFile.id, sheetName, "A1:X20")
    }

    fun getProgress(): Int {
        return (processedSheetCount.get() * 100) / totalSheetCount
    }


    private fun getTemplates(): List<File> {
        return driveAdapter
            .listFiles()
            .filter { it.name.startsWith("P") }
    }
}