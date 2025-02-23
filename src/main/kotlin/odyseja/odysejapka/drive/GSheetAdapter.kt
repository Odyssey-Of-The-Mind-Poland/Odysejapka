package odyseja.odysejapka.drive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.ValueRange
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
internal class GSheetAdapter : SheetAdapter {
    private var credentials: Credential = CredentialsProvider().getCredentials()
    private var jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    private var httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private var service: Sheets = Sheets.Builder(httpTransport, jsonFactory, credentials)
        .setApplicationName("gad")
        .build()

    override fun getSheet(sheetsId: String): MutableList<Sheet>? {
        return service.spreadsheets().get(sheetsId).execute().sheets
    }

    override fun getValue(sheetId: String, sheetName: String, range: String): MutableList<MutableList<String>> {
        val values = service.spreadsheets().values().get(sheetId, "$sheetName!$range").execute().getValues()
            ?: return mutableListOf()

        val stringValues = mutableListOf<MutableList<String>>()
        values.forEach({ row -> stringValues.add(row.map { it.toString() }.toMutableList()) }
        )
        return stringValues
    }

    override fun writeValue(
        sheetId: String,
        sheetName: String,
        range: String,
        value: String,
        inputType: String
    ) {
        writeValues(sheetId, sheetName, range, listOf(value), inputType)
    }

    override fun writeValues(
        sheetId: String,
        sheetName: String,
        range: String,
        writeValues: List<String>,
        inputType: String
    ) {
        val values = listOf(writeValues)
        val body = ValueRange().setValues(values)
        service.spreadsheets().values().update(sheetId, "$sheetName!$range", body).setValueInputOption(inputType)
            .execute()
    }
}