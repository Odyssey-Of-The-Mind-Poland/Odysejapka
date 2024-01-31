import com.google.api.client.json.gson.GsonFactory

internal class GadConfiguration(
    val templatesFolderId: String,
    val destinationFolderId: String,
    val zspId: String,
    val problemPunctuationCells: Map<String, PunctationCells>
) {

    fun gadService(): GadService {
        val credentials = CredentialsProvider().getCredentials()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val driveAdapter = DriveAdapter(credentials, jsonFactory, templatesFolderId, destinationFolderId)
        val sheetsAdapter = SpreadSheetsAdapter(credentials, jsonFactory, zspId)
        return GadService(driveAdapter, sheetsAdapter, problemPunctuationCells)
    }
}