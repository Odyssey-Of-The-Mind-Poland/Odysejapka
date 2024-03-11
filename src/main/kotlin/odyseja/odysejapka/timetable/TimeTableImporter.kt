import com.google.api.services.sheets.v4.model.Sheet
import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.timetable.TimeTableService
import java.util.concurrent.atomic.AtomicInteger

internal class TimeTableImporter(
    private val timeTableService: TimeTableService,
    private val sheetsAdapter: ZspSheetsAdapter,
    private val city: String
) {

    private var totalSheetCount = 0
    private var processedSheetCount = AtomicInteger(0)

    fun startImporting() {
        val sheets = sheetsAdapter.getSheets()
        totalSheetCount = sheets?.size ?: 1
        for (sheet in sheets!!) {
            processSheet(sheet)
            processedSheetCount.incrementAndGet()
        }
    }

    fun getProgress(): Int {
        return (processedSheetCount.get() * 100) / totalSheetCount
    }

    private fun processSheet(sheet: Sheet) {
        val title = sheet.properties.title
        println("Processing: $title")

        val teams = sheetsAdapter.getTeams(title)

        processTeams(teams)
    }

    private fun processTeams(teams: Teams) {
        for (team in teams.teams) {
            timeTableService.addPerformance(team.toPerformance(city))
        }
    }

    private fun getZspValue(sheetId: String, cell: String): String {
        return "=importrange(\"https://docs.google.com/spreadsheets/d/$sheetId\";\"Arkusz Ocen Surowych!$cell\")"
    }
}