import com.google.api.client.json.gson.GsonFactory
import odyseja.odysejapka.drive.CredentialsProvider
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.ZspSheetsAdapter

internal class GadConfiguration(
    val templatesFolderId: String,
    val destinationFolderId: String,
    val zspId: String,
    val problemPunctuationCells: Map<String, PunctationCells>
) {

    fun gadRunner(): GadRunner {
        val credentials = CredentialsProvider().getCredentials()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val driveAdapter = DriveAdapter(credentials, jsonFactory, templatesFolderId)
        val sheetsAdapter = ZspSheetsAdapter(credentials, jsonFactory, zspId)
        return GadRunner(driveAdapter, sheetsAdapter, problemPunctuationCells, destinationFolderId)
    }
}