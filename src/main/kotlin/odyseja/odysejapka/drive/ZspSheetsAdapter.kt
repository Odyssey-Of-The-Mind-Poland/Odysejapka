package odyseja.odysejapka.drive

import Team
import Teams
import com.google.api.services.sheets.v4.model.Sheet

class ZspSheetsAdapter(
    private val sheetAdapter: SheetAdapter,
    private val zspId: String
) {

    companion object {
        fun getZspSheetsAdapter(zspId: String): ZspSheetsAdapter {
            return ZspSheetsAdapter(SheetAdapter.getSheetAdapter(), zspId)
        }
    }

    fun writeZsp(cell: String, value: List<String>, sheetName: String) {
        sheetAdapter.writeValues(zspId, sheetName, cell, value)
    }

    fun writeCell(cell: String, value: String, sheetId: String) {
        listOf(listOf(value))
        sheetAdapter.writeValue(sheetId, "Arkusz Ocen Cząstkowych", cell, value, "RAW")
    }

    fun getCellValue(cell: String, sheetId: String): String {
        val values = sheetAdapter.getValue(sheetId, "Arkusz Ocen Cząstkowych", cell)
        return values[0][0]
    }


    fun getSheets(): MutableList<Sheet>? {
        return sheetAdapter.getSheet(zspId)
    }

    fun getNumericalValue(row: List<Any>, size: Int): Float {
        try {
            if (row.size - 1 > size - 1) {
                val value = row[size].toString().replace(',', '.').toFloat()
                return value
            }

        } catch (e: Exception) {
            println("Could not convert ${row[size]}")
        }
        return 0f

    }

    fun getAllTeams(): List<Team> {
        val sheetsAdapter = getZspSheetsAdapter(zspId)
        val sheets = sheetsAdapter.getSheets()
        val allTeams: MutableList<Team> = mutableListOf()
        for (sheet in sheets!!) {
            val title = sheet.properties.title
            println("Processing sheet: $title")
            val teams = getTeams(title)
            val teamsFromSheet = teams.teams
            allTeams.addAll(teamsFromSheet)
        }
        return allTeams
    }

    fun getTeams(sheetName: String): Teams {
        val values = sheetAdapter.getValue(zspId, sheetName, "A1:P")
        val teams = mutableListOf<Team>()
        var judges = ""
        var day = ""
        var stage = 1
        for ((i, row) in values.withIndex()) {
            if (row.size > 0 && isJudge(row[0])){
                if (row.size == 1){
                    judges = "" // For Regional Finals Judges names are not printed onto the scoring sheet => Judges section is empty = we don't print it
                    break
                }
                else if (row.size > 1){
                    judges = row[1] // For Country Finals Judges names will be used
                    break
                }
            }

            if (row.size > 0 && isDay(row[0])) {
                day = row[0]
            }

            if (row.size > 0 && isStage(row[0])) {
                stage = row[0].split(" ")[1].toInt()
            }

            if (row.size == 0 || !isTime(row[0])) {
                continue
            }

            val team = Team(
                    performanceHour = row[0],
                    spontanHour = row[1],
                    code = row[2],
                    membershipNumber = row[3],
                    league = row[4],
                    part = row[5],
                    teamName = row[6],
                    shortTeamName = row[7],
                    city = row[8],
                    zspRow = i + 1,
                    day = day,
                    stage = stage,
                    zspSheet = sheetName,
                    longTermScore = getNumericalValue(row, 10),
                    styleScore = getNumericalValue(row, 11),
                    penaltyScore = getNumericalValue(row, 12),
                    weightHeld = getNumericalValue(row, 13),
                    spontaneousScore = getNumericalValue(row, 15),
                )
            teams.add(
                team
            )


        }
        return Teams(judges, teams)
    }

    private fun isJudge(judge: String): Boolean {
        return judge.contains("SĘDZIOWIE")
    }

    private fun isTime(cell: String): Boolean {
        val regex = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$".toRegex()
        return regex.matches(cell)
    }

    private fun isDay(cell: String): Boolean {
        return cell.lowercase().contains("sobota") || cell.lowercase().contains("niedziela")
    }

    fun isStage(cell: String): Boolean {
        return cell.lowercase().contains("scena")
    }
}