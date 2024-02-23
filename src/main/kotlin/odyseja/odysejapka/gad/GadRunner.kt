import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.model.Sheet
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SpreadSheetsAdapter
import java.util.concurrent.atomic.AtomicInteger

internal class GadRunner(
    private val driveAdapter: DriveAdapter,
    private val sheetsAdapter: SpreadSheetsAdapter,
    private val problemPunctuationCells: Map<String, PunctationCells>
) {

    private val templates = getTemplates()
    private var totalSheetCount = 0
    private var processedSheetCount = AtomicInteger(0)

    fun createForms() {
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

        processTeams(teams, title)
    }

    private fun processTeams(teams: Teams, sheetTitle: String) {
        for (team in teams.teams) {
            val template = getTemplate(team.getProblem()[0])

            val file = driveAdapter.copyFile(template.id, team.getFileName())
            templateCell(file.id, "A1", team.getAge())
            templateCell(file.id, "A2", team.teamName)
            templateCell(file.id, "A3", teams.judges)

            val cells = problemPunctuationCells[team.getProblem()]!!
            val values = listOf(
                getZspValue(file.id, cells.dt),
                getZspValue(file.id, cells.style),
                getZspValue(file.id, cells.penalty)
            )
            sheetsAdapter.writeZsp("I${team.zspRow}:K${team.zspRow}", values, sheetTitle)
            println("Created: ${file.name}")
        }
    }

    private fun getZspValue(sheetId: String, cell: String): String {
        return "=importrange(\"https://docs.google.com/spreadsheets/d/$sheetId\";\"Arkusz Ocen Surowych!$cell\")"
    }

    private fun templateCell(sheetId: String, cell: String, value: String) {
        val cellValue = sheetsAdapter.getCellValue(cell, sheetId)
        sheetsAdapter.writeCell(cell, cellValue.replace("XXX", value), sheetId)
    }

    private fun getTemplate(problem: Char): File {
        return templates[problem]!!
    }

    private fun getTemplates(): Map<Char, File> {
        return driveAdapter
            .listFiles()
            .filter { it.name.endsWith("_KOD_NAZWA") }
            .associateBy { it.name[1] }
    }
}