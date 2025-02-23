package odyseja.odysejapka.sak

import Team
import com.google.api.services.drive.model.File
import odyseja.odysejapka.async.Runner
import odyseja.odysejapka.drive.*
import org.springframework.context.ApplicationContext
import java.util.concurrent.atomic.AtomicInteger

internal class SakRunner(
    private val driveAdapter: DriveAdapter,
    private val sheetAdapter: SheetAdapter,
    private val zspId: String,
    private val templatesFolderId: String,
    private val zspSheetsAdapter: ZspSheetsAdapter
) : Runner {

    private val templates = getTemplates()
    private var totalTeamsCount = 0
    private var processedTeamsCount = AtomicInteger(0)


    override fun run() {
        val teams = zspSheetsAdapter.getAllTeams()
        totalTeamsCount = teams.size
        val groups = teams.groupBy { SpontanGroups.Group.fromTeam(it) }.map { (group, performances) ->
            SpontanGroups(group, performances)
        }
        for (group in groups) {

            if (group.group.age == "0" || group.group.problem == "0") {
                continue
            }

            processGroup(group)
            Thread.sleep(2000) // google API needs this
        }
    }

    private fun processGroup(group: SpontanGroups) {
        val sheetFiles = templates.filter { it.name.startsWith(group.groupCode()) }
        if (sheetFiles.isEmpty()) {
            processedTeamsCount.addAndGet(group.performances.size)
            return
        }
        val sheetFile = sheetFiles[0]
        val sheetName = sheetAdapter.getSheet(sheetFile.id)!!.get(0).properties.title
        val values = sheetAdapter.getValue(sheetFile.id, sheetName, "A1:X20")

        var teamStartCell = findCell(values, "Drużyna")
        var pointsCell = findCell(values, "suma punktów")
        val teams = group.performances.sortedBy { it.spontanSort() }
        for (team in teams) {

            if (team.isForeigner()) {
                continue
            }

            teamStartCell = Pair(teamStartCell.first, teamStartCell.second + 1)
            pointsCell = Pair(pointsCell.first, pointsCell.second + 1)
            processTeam(team, sheetName, sheetFile.id, teamStartCell, pointsCell)
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
        sheetAdapter.writeValue(sheetId, sheetName, "${teamStartCell.first}${teamStartCell.second}", team.teamName)
        sheetAdapter.writeValue(
            zspId,
            team.zspSheet!!,
            "P${team.zspRow}",
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


    private fun getTemplates(): List<File> {
        return driveAdapter
            .listFiles(templatesFolderId)
            .filter { it.name.startsWith("P") }
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
        return "=importrange(\"https://docs.google.com/spreadsheets/d/$sheetId\";\"$sheetName!$cell\")"
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