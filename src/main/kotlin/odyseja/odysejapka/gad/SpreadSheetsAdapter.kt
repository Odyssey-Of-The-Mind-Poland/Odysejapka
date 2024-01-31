import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.ValueRange

internal class SpreadSheetsAdapter(
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

    fun geTeams(sheetName: String): Teams {
        val values = service.spreadsheets().values().get(zspId, "$sheetName!A1:C").execute().getValues()
        val teams = mutableListOf<Team>()
        var judges = ""
        for ((i, row) in values.withIndex()) {

            if (row.size > 0 && isJudge(row[0].toString())) {
                judges = row[1].toString()
                break
            }

            if (row.size == 0 || !isDate(row[0].toString())) {
                continue
            }

            teams.add(Team(row[0].toString(), row[1].toString(), row[2].toString(), i + 1))
        }
        return Teams(judges, teams)
    }

    private fun isJudge(judge: String): Boolean {
        return judge.contains("SĘDZIOWIE")
    }

    private fun isDate(cell: String): Boolean {
        val regex = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$".toRegex()
        return regex.matches(cell)
    }
}