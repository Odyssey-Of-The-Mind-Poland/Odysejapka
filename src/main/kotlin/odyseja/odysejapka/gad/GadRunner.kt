import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.model.Sheet
import odyseja.odysejapka.async.Runner
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.gad.GadGroup
import java.util.concurrent.atomic.AtomicInteger

internal class GadRunner(
    private val driveAdapter: DriveAdapter,
    private val sheetsAdapter: ZspSheetsAdapter,
    private val problemPunctuationCells: Map<String, PunctationCells>,
    private val destinationFolderId: String,
    private val templatesFolderId: String
) : Runner {

    private val templates = getTemplates()
    private var totalSheetCount = 1
    private var processedSheetCount = AtomicInteger(0)
    private val groupFolders = mutableMapOf<GadGroup, String>()

    override fun run() {
        val sheets = sheetsAdapter.getSheets()
        totalSheetCount = sheets?.size ?: 1
        for (sheet in sheets!!) {
            processSheet(sheet)
            processedSheetCount.incrementAndGet()
        }
        processedSheetCount.set(totalSheetCount)
    }

    override fun getProgress(): Int {
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

            if (team.isJunior()) {
                continue
            }

            val groupFolderId = groupFolders.computeIfAbsent(team.getGroup()) {
                driveAdapter.createFolder(it.getDirName(), destinationFolderId)
            }

            val template = getTemplate(team.getProblem()[0])

            val file = driveAdapter.copyFile(template.id, team.getFileName(), groupFolderId)
            templateCell(file.id, "A1", team.getAge())
            templateCell(file.id, "A2", team.teamName)
            templateCell(file.id, "A3", teams.judges)

            val cells = problemPunctuationCells[team.getProblem()]!!
            val values = listOf(
                getZspValue(file.id, cells.dt),
                getZspValue(file.id, cells.style),
                getZspValue(file.id, cells.penalty),
                getBalsaValue(file.id, cells.balsa),
                    getZspValue(file.id, cells.anomaly),
            )
            sheetsAdapter.writeZsp("K${team.zspRow}:N${team.zspRow}", values, sheetTitle)
            println("Created: ${file.name}")
        }
    }

    private fun getZspValue(sheetId: String, cell: String): String {
        return "=importrange(\"https://docs.google.com/spreadsheets/d/$sheetId\";\"Arkusz Ocen Surowych!$cell\")"
    }

    private fun getBalsaValue(sheetId: String, cell: String?): String {
        if (cell.isNullOrBlank()) {
            return ""
        }
        return "=importrange(\"https://docs.google.com/spreadsheets/d/$sheetId\";\"Arkusz Ocen Cząstkowych!$cell\")"
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
            .listFiles(templatesFolderId)
            .filter { it.name.endsWith("_KOD_NAZWA") }
            .associateBy { it.name[1] }
    }
}