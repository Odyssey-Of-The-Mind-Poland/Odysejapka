package odyseja.odysejapka.sak

import odyseja.odysejapka.gad.Team
import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.model.Sheet
import odyseja.odysejapka.async.AsyncLogger
import odyseja.odysejapka.async.CancellableRunner
import odyseja.odysejapka.async.Log
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.drive.SpontanGroups
import odyseja.odysejapka.drive.ZspSheetsAdapter
import java.util.concurrent.atomic.AtomicInteger

internal class SakRunner(
    private val driveAdapter: DriveAdapter,
    private val sheetsAdapter: SheetAdapter,
    private val zspId: String,
    private val templatesFolderId: String
) : CancellableRunner {

    private val templates = getTemplates()
    private val cancelled = java.util.concurrent.atomic.AtomicBoolean(false)
    private val sheetMap: Map<String, Pair<Sheet, String>> = buildSheetMap()
    private var totalTeamsCount = 0
    private var processedTeamsCount = AtomicInteger(0)
    private val logger = AsyncLogger()


    override fun run() {
        val teams = ZspSheetsAdapter.getZspSheetsAdapter(zspId).getAllTeams()
        totalTeamsCount = teams.size
        val groups = teams.groupBy { SpontanGroups.Group.fromTeam(it) }.map { (group, performances) ->
            SpontanGroups(group, performances)
        }
        for (group in groups) {
            if (cancelled.get()) return
            if (group.group.age == "0" || group.group.problem == "0") {
                continue
            }
            try {
                logger.log("Started processing problem ${group.group.problem} division ${group.group.age}")
                processGroup(group)
                logger.log("Finished processing problem ${group.group.problem} division ${group.group.age}")
            } catch (e: Exception) {
                logger.error("Error processing problem ${group.group.problem} division ${group.group.age}: ${e.message ?: e}")
            }
            Thread.sleep(2000) // google API needs this
        }
    }

    override fun requestCancel() {
        cancelled.set(true)
    }

    override fun isCancelled(): Boolean = cancelled.get()

    private fun processGroup(group: SpontanGroups) {
        val groupCode = group.groupCode()
        val sheetEntry = sheetMap.entries.find { it.key == groupCode }?.value
        if (sheetEntry == null) {
            processedTeamsCount.addAndGet(group.performances.size)
            return
        }
        val (sheet, fileId) = sheetEntry
        val sheetName = sheet.properties.title
        val values = sheetsAdapter.getValue(fileId, sheetName, "A1:AZ20")

        var teamStartCell = findCell(values, "Drużyna")
        var pointsCell = findCell(values, "Ostateczny wynik").copy(second = teamStartCell.second)
        val teams = group.performances.sortedBy { it.spontanSort() }
        for (team in teams) {
            if (cancelled.get()) return
            if (team.isForeigner()) {
                continue
            }

            teamStartCell = Pair(teamStartCell.first, teamStartCell.second + 1)
            pointsCell = Pair(pointsCell.first, pointsCell.second + 1)
            try {
                processTeam(team, sheetName, fileId, teamStartCell, pointsCell)
            } catch (e: Exception) {
                logger.error("Error processing team ${team.teamName}: ${e.message ?: e}")
            }
            Thread.sleep(5000)
        }
    }

    private fun processTeam(
        team: Team,
        sheetName: String,
        sheetId: String,
        teamStartCell: Pair<String, Int>,
        pointsCell: Pair<String, Int>
    ) {
        sheetsAdapter.writeValue(sheetId, sheetName, "${teamStartCell.first}${teamStartCell.second}", team.teamName)
        sheetsAdapter.writeValue(
            zspId,
            team.zspSheet!!,
            "T${team.zspRow}",
            getZspValue(sheetId, sheetName, "${pointsCell.first}${pointsCell.second}")
        )
        processedTeamsCount.incrementAndGet()
    }

    override fun getProgress(): Int {
        if (totalTeamsCount != 0) {
            return (processedTeamsCount.get() * 100) / totalTeamsCount
        }
        return 0
    }

    override fun getLogs(): List<Log> {
        return logger.getLogs()
    }


    private fun getTemplates(): List<File> {
        return driveAdapter
            .listFiles(templatesFolderId)
    }

    private fun buildSheetMap(): Map<String, Pair<Sheet, String>> {
        val map = mutableMapOf<String, Pair<Sheet, String>>()
        for (templateFile in templates) {
            val sheets = sheetsAdapter.getSheet(templateFile.id)
            sheets?.forEach { sheet ->
                val sheetName = sheet.properties.title
                map[sheetName] = Pair(sheet, templateFile.id)
            }
        }
        return map
    }

    private fun findCell(values: List<List<String>>, cellValue: String): Pair<String, Int> {
        for (i in values.indices) {
            for (j in values[i].indices) {
                if (values[i][j].contains(cellValue)) {
                    return Pair(columnIndexToExcelLetter(j), (i + 1))
                }
            }
        }
        throw IllegalArgumentException("Cell not found")
    }

    private fun getZspValue(sheetId: String, sheetName: String, cell: String): String {
        return "=importrange(\"https://docs.google.com/spreadsheets/d/$sheetId\";\"${sheetName}!$cell\")"
    }

    private fun columnIndexToExcelLetter(index: Int): String {
        var columnIndex = index
        val letters = StringBuilder()
        while (columnIndex >= 0) {
            val remainder = columnIndex % 26
            letters.insert(0, ('A' + remainder))
            columnIndex = (columnIndex / 26) - 1
        }
        return letters.toString()
    }
}