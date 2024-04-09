package odyseja.odysejapka.drive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.ValueRange

class SheetAdapter(
    credentials: Credential,
    jsonFactory: JsonFactory,
) {

    companion object {
        fun getSheetAdapter(): SheetAdapter {
            val credentials = CredentialsProvider().getCredentials()
            val jsonFactory = GsonFactory.getDefaultInstance()
            return SheetAdapter(credentials, jsonFactory)
        }
    }

    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val service: Sheets = Sheets.Builder(httpTransport, jsonFactory, credentials)
        .setApplicationName("gad")
        .build()

    fun getSheet(sheetsId: String): MutableList<Sheet>? {
        return service.spreadsheets().get(sheetsId).execute().sheets
    }

    fun getValue(sheetId: String, sheetName: String, range: String): MutableList<MutableList<String>> {
        val values = service.spreadsheets().values().get(sheetId, "$sheetName!$range").execute().getValues()
            ?: return mutableListOf()

        val stringValues = mutableListOf<MutableList<String>>()
        values.forEach({ row -> stringValues.add(row.map { it.toString() }.toMutableList()) }
        )
        return stringValues
    }

    fun writeValue(
        sheetId: String,
        sheetName: String,
        range: String,
        value: String,
        inputType: String = "USER_ENTERED"
    ) {
        writeValues(sheetId, sheetName, range, listOf(value), inputType)
    }

    fun writeValues(
        sheetId: String,
        sheetName: String,
        range: String,
        writeValues: List<String>,
        inputType: String = "USER_ENTERED"
    ) {
        val values = listOf(writeValues)
        val body = ValueRange().setValues(values)
        service.spreadsheets().values().update(sheetId, "$sheetName!$range", body).setValueInputOption(inputType)
            .execute()
    }
}