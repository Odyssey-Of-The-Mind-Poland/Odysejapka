package odyseja.odysejapka.drive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class DriveAdapter(
    credentials: Credential,
    jsonFactory: JsonFactory
) {

    companion object {
        fun getDriveAdapter(): DriveAdapter {
            val credentials = CredentialsProvider().getCredentials()
            val jsonFactory = GsonFactory.getDefaultInstance()
            return DriveAdapter(credentials, jsonFactory)
        }
    }

    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val service: Drive = Drive.Builder(httpTransport, jsonFactory, credentials)
        .setApplicationName("gad")
        .build()

    fun listFiles(folderId: String): List<File> {
        return service.files().list()
            .setQ("parents in '$folderId'")
            .execute()
            .files
    }

    fun copyFile(fileId: String, newName: String, destination: String): File {
        val file = File()
        file.name = newName
        file.parents = listOf(destination)
        return service.Files().copy(fileId, file).execute()
    }

    fun createFolder(folderName: String, parentDir: String): String {
        val fileMetadata = File()
        fileMetadata.name = folderName
        fileMetadata.mimeType = "application/vnd.google-apps.folder"
        fileMetadata.parents = listOf(parentDir)
        return service.files().create(fileMetadata).execute().id
    }
}
