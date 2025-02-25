package odyseja.odysejapka.drive

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.nio.file.Path
import kotlin.io.path.exists

@SpringBootTest
@ActiveProfiles("test")
class FileAdaptersTest {

    @Autowired
    private lateinit var driveAdapter: InMemoryDriveAdapter


    @Test
    fun `test create and list folders`() {
        val folderId = driveAdapter.createFolder("test-folder", "")
        driveAdapter.createSpreadsheet("file", folderId)

        val files = driveAdapter.listFiles(folderId)
        assertEquals(1, files.size)
        assertEquals("file", files[0].name)
    }

    @Test
    fun `test create and read sheet`() {
        val spreadsheetId = driveAdapter.createSpreadsheet("test-spreadsheet", "")

        driveAdapter.writeValues(
            spreadsheetId,
            "Sheet1",
            "A1:B2",
            listOf("1", "2", "3", "4"),
            "RAW"
        )

        val sheets = driveAdapter.getSheet(spreadsheetId)
        assertNotNull(sheets)
        assertEquals(1, sheets!!.size)
        assertEquals("Sheet1", sheets[0].properties.title)

        val values = driveAdapter.getValue(spreadsheetId, "Sheet1", "A1:B2")
        assertEquals(2, values.size) // 2 rows
        assertEquals(2, values[0].size) // 2 columns
        assertEquals("1", values[0][0])
        assertEquals("2", values[0][1])
        assertEquals("3", values[1][0])
        assertEquals("4", values[1][1])
    }

    @Test
    fun `test copy file`() {
        val sourceId = driveAdapter.createFolder("source", "")
        val destId = driveAdapter.createFolder("dest", "")

        val sheetId = driveAdapter.createSpreadsheet("test-sheet", sourceId)
        driveAdapter.writeValue(sheetId, "Sheet1", "A1", "test", "RAW")

        val copiedFile = driveAdapter.copyFile(sheetId, "copied-sheet", destId)
        assertEquals("copied-sheet", copiedFile.name)

        val values = driveAdapter.getValue(copiedFile.id, "Sheet1", "A1")
        assertEquals("test", values[0][0])
    }

    @Test
    fun `test copy directory`() {
        val sourceId = driveAdapter.createFolder("source-dir", "")
        val destId = driveAdapter.createFolder("dest-dir", "")

        val subFolderId = driveAdapter.createFolder("subfolder", sourceId)
        val sheetId = driveAdapter.createSpreadsheet("test-sheet", subFolderId)
        driveAdapter.writeValue(sheetId, "Sheet1", "A1", "test-content", "RAW")

        val copiedDir = driveAdapter.copyFile(sourceId, "copied-dir", destId)
        assertEquals("copied-dir", copiedDir.name)
        assertEquals("application/vnd.google-apps.folder", copiedDir.mimeType)

        val copiedFiles = driveAdapter.listFiles(destId)
        assertEquals(1, copiedFiles.size)
        assertEquals("copied-dir", copiedFiles[0].name)

        val copiedSubFiles = driveAdapter.listFiles(copiedFiles[0].id)
        assertEquals(1, copiedSubFiles.size)
        assertEquals("subfolder", copiedSubFiles[0].name)

        val copiedSheetFiles = driveAdapter.listFiles(copiedSubFiles[0].id)
        assertEquals(1, copiedSheetFiles.size)
        assertEquals("test-sheet", copiedSheetFiles[0].name)
        val values = driveAdapter.getValue(copiedSheetFiles[0].id, "Sheet1", "A1")
        assertEquals("test-content", values[0][0])
    }

    @Test
    fun `load spreadsheet from resources`() {
        val dirId = driveAdapter.createFolder("test-folder", "")
        val sheetId = driveAdapter.loadSpreadsheetFromResources("test-spreadsheet", dirId)

        val value = driveAdapter.getValue(sheetId, "SCENA4", "A1")
        assertEquals("SCENA 4", value[0][0])
    }
} 