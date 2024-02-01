import com.google.api.client.json.gson.GsonFactory

internal class GadConfiguration(
    val templatesFolderId: String,
    val destinationFolderId: String,
    val zspId: String,
    val problemPunctuationCells: Map<String, PunctationCells>
) {

    fun gadRunner(): GadRunner {
        val credentials = CredentialsProvider().getCredentials()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val driveAdapter = DriveAdapter(credentials, jsonFactory, templatesFolderId, destinationFolderId)
        val sheetsAdapter = SpreadSheetsAdapter(credentials, jsonFactory, zspId)
        return GadRunner(driveAdapter, sheetsAdapter, problemPunctuationCells)
    }
}