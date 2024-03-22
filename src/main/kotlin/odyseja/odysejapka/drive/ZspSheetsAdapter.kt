package odyseja.odysejapka.drive

import Team
import Teams
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.ValueRange

class ZspSheetsAdapter(
    credentials: Credential,
    jsonFactory: JsonFactory,
    private val zspId: String
) {

    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val service: Sheets = Sheets.Builder(httpTransport, jsonFactory, credentials)
        .setApplicationName("gad")
        .build()

    fun writeZsp(row: String, value: List<String>, sheetName: String) {
        val range = "$sheetName!$row"
        val valueRange = listOf(value)
        service.spreadsheets().values().update(zspId, range, ValueRange().setValues(valueRange))
            .setValueInputOption("USER_ENTERED")
            .execute()
    }

    fun writeCell(row: String, value: String, sheetId: String) {
        val range = "Arkusz Ocen Cząstkowych!$row"
        val valueRange = listOf(listOf(value))
        service.spreadsheets().values().update(sheetId, range, ValueRange().setValues(valueRange))
            .setValueInputOption("RAW")
            .execute()
    }

    fun getCellValue(row: String, sheetId: String): String {
        val values =
            service.spreadsheets().values().get(sheetId, "Arkusz Ocen Cząstkowych!$row").execute()
                .getValues()
        return values[0][0].toString()
    }


    fun getSheets(): MutableList<Sheet>? {
        return service.spreadsheets().get(zspId).execute().sheets
    }

    fun getTeams(sheetName: String): Teams {
        val values = service.spreadsheets().values().get(zspId, "$sheetName!A1:N").execute().getValues()
        val teams = mutableListOf<Team>()
        var judges = ""
        var day = ""
        var stage = 1
        for ((i, row) in values.withIndex()) {

            if (row.size > 0 && isJudge(row[0].toString())) {
                judges = row[1].toString()
                break
            }

            if (row.size > 0 && isDay(row[0].toString())) {
                day = row[0].toString()
            }

            if (row.size > 0 && isStage(row[0].toString())) {
                stage = row[0].toString().split(" ")[1].toInt()
            }

            if (row.size == 0 || !isTime(row[0].toString())) {
                continue
            }

            teams.add(
                Team(
                    performanceHour = row[0].toString(),
                    spontanHour = row[1].toString(),
                    code = row[2].toString(),
                    membershipNumber = row[3].toString(),
                    league = row[4].toString(),
                    part = row[5].toString(),
                    teamName = row[6].toString(),
                    zspRow = i + 1,
                    day=day,
                    stage=stage,
                    zspSheet =sheetName,
                    longTermScore = row[9].toString(),
                    styleScore = row[10].toString(),
                    penaltyScore = row[11].toString(),
                    spontaneousScore = row[12].toString(),
                )
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

    private fun isStage(cell: String): Boolean {
        return cell.lowercase().contains("scena")
    }
}