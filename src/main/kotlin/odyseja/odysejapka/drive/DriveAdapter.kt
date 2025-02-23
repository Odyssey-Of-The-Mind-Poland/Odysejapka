package odyseja.odysejapka.drive

import com.google.api.services.drive.model.File

interface DriveAdapter {
    fun listFiles(folderId: String): List<File>
    fun copyFile(fileId: String, newName: String, destination: String): File
    fun createFolder(folderName: String, parentDir: String): String
}