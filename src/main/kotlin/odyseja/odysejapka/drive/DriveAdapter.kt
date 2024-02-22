package odyseja.odysejapka.drive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File

class DriveAdapter(
    credentials: Credential,
    jsonFactory: JsonFactory,
    val fromFolderId: String,
    val toFolderId: String
) {

    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val service: Drive = Drive.Builder(httpTransport, jsonFactory, credentials)
        .setApplicationName("gad")
        .build()

    fun listFiles(): List<File> {
        return service.files().list()
            .setQ("parents in '$fromFolderId'")
            .execute()
            .files
    }

    fun copyFile(fileId: String, newName: String): File {
        val file = File()
        file.name = newName
        file.parents = listOf(toFolderId)
        return service.Files().copy(fileId, file).execute()
    }
}
