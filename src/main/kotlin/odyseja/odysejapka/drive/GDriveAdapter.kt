package odyseja.odysejapka.drive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
internal class GDriveAdapter : DriveAdapter {

    private var credentials: Credential = CredentialsProvider().getCredentials()
    private var jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
    private var httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private var service: Drive = Drive.Builder(httpTransport, jsonFactory, credentials)
        .setApplicationName("gad")
        .build()

    override fun listFiles(folderId: String): List<File> {
        return service.files().list()
            .setQ("parents in '$folderId'")
            .execute()
            .files
    }

    override fun copyFile(fileId: String, newName: String, destination: String): File {
        val file = File()
        file.name = newName
        file.parents = listOf(destination)
        return service.Files().copy(fileId, file).execute()
    }

    override fun createFolder(folderName: String, parentDir: String): String {
        val fileMetadata = File()
        fileMetadata.name = folderName
        fileMetadata.mimeType = "application/vnd.google-apps.folder"
        fileMetadata.parents = listOf(parentDir)
        return service.files().create(fileMetadata).execute().id
    }
}
