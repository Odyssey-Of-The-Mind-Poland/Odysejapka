package odyseja.odysejapka.drive

import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.model.Sheet
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class InMemoryDriveAdapter : DriveAdapter, SheetAdapter {

    data class InMemoryFile(
        val id: String,
        var name: String,
        val mimeType: String,
        var parentId: String?
    )

    private val spreadsheetStorage = mutableMapOf<String, MutableMap<String, MutableList<MutableList<String>>>>()

    private val driveStorage = mutableMapOf<String, InMemoryFile>()

    private val idCounter = AtomicLong(1)

    override fun listFiles(folderId: String): List<File> {
        return driveStorage.values
            .filter { it.parentId == folderId }
            .map { toApiFile(it) }
    }

    override fun copyFile(fileId: String, newName: String, destination: String): File {
        val original = driveStorage[fileId]
            ?: throw IllegalArgumentException("File with id=$fileId not found")

        return if (original.mimeType == FOLDER_MIME) {
            copyFolder(original, newName, destination)
        } else {
            copySingleFile(original, newName, destination)
        }
    }

    override fun createFolder(folderName: String, parentDir: String): String {
        val newId = nextId()
        val folder = InMemoryFile(
            id = newId,
            name = folderName,
            mimeType = FOLDER_MIME,
            parentId = parentDir.ifBlank { null }
        )
        driveStorage[newId] = folder
        return newId
    }


    private fun copyFolder(original: InMemoryFile, newName: String, destination: String): File {
        val newFolderId = nextId()
        val newFolder = InMemoryFile(
            id = newFolderId,
            name = newName,
            mimeType = FOLDER_MIME,
            parentId = destination.ifBlank { null }
        )
        driveStorage[newFolderId] = newFolder

        val children = driveStorage.values.filter { it.parentId == original.id }

        children.forEach { child ->
            copyFile(child.id, child.name, newFolderId)
        }

        return toApiFile(newFolder)
    }

    private fun copySingleFile(original: InMemoryFile, newName: String, destination: String): File {
        val newId = nextId()
        val copy = InMemoryFile(
            id = newId,
            name = newName,
            mimeType = original.mimeType,
            parentId = destination.ifBlank { null }
        )
        driveStorage[newId] = copy

        if (original.mimeType == SPREADSHEET_MIME) {
            spreadsheetStorage[original.id]?.let { originalSheets ->
                val newSheets = mutableMapOf<String, MutableList<MutableList<String>>>()
                for ((sheetName, rows) in originalSheets) {
                    val newRows = rows.map { row -> row.toMutableList() }.toMutableList()
                    newSheets[sheetName] = newRows
                }
                spreadsheetStorage[newId] = newSheets
            }
        }

        return toApiFile(copy)
    }


    fun createSpreadsheet(name: String, parentDir: String): String {
        val newId = nextId()
        val spreadsheet = InMemoryFile(
            id = newId,
            name = name,
            mimeType = SPREADSHEET_MIME,
            parentId = parentDir.ifBlank { null }
        )
        driveStorage[newId] = spreadsheet
        spreadsheetStorage[newId] = mutableMapOf()
        return newId
    }

    override fun getSheet(sheetsId: String): MutableList<Sheet>? {
        val sheetsMap = spreadsheetStorage[sheetsId] ?: return null
        return sheetsMap.keys.map { sheetName ->
            Sheet().apply {
                properties = com.google.api.services.sheets.v4.model.SheetProperties().apply {
                    title = sheetName
                }
            }
        }.toMutableList()
    }

    override fun getValue(sheetId: String, sheetName: String, range: String): MutableList<MutableList<String>> {
        val sheetsMap = spreadsheetStorage[sheetId] ?: return mutableListOf()
        val rows = sheetsMap[sheetName] ?: return mutableListOf()

        val (startRow, startCol, endRow, endCol) = parseRange(range)
        val result = mutableListOf<MutableList<String>>()

        for (r in startRow..endRow) {
            val rowValues = mutableListOf<String>()
            if (r < rows.size) {
                val row = rows[r]
                for (c in startCol..endCol) {
                    rowValues.add(if (c < row.size) row[c] else "")
                }
            } else {
                for (c in startCol..endCol) {
                    rowValues.add("")
                }
            }
            result.add(rowValues)
        }

        return result
    }

    override fun writeValue(sheetId: String, sheetName: String, range: String, value: String, inputType: String) {
        val (startRow, startCol, _, _) = parseRange(range)

        val sheetsMap = spreadsheetStorage.getOrPut(sheetId) { mutableMapOf() }
        val rows = sheetsMap.getOrPut(sheetName) { mutableListOf() }

        ensureSize(rows, startRow, startCol)
        rows[startRow][startCol] = value
    }

    override fun writeValues(
        sheetId: String,
        sheetName: String,
        range: String,
        writeValues: List<String>,
        inputType: String
    ) {
        val (startRow, startCol, endRow, endCol) = parseRange(range)

        val sheetsMap = spreadsheetStorage.getOrPut(sheetId) { mutableMapOf() }
        val rows = sheetsMap.getOrPut(sheetName) { MutableList(100) { MutableList(100) { "" } } }

        ensureSize(rows, endRow, endCol)

        val totalRows = (endRow - startRow) + 1
        val totalCols = (endCol - startCol) + 1
        val rangeSize = totalRows * totalCols

        val iterator = writeValues.iterator()

        for (r in 0 until totalRows) {
            for (c in 0 until totalCols) {
                val rowIndex = startRow + r
                val colIndex = startCol + c
                if (iterator.hasNext()) {
                    rows[rowIndex][colIndex] = iterator.next()
                } else {
                    rows[rowIndex][colIndex] = ""
                }
            }
        }
    }

    private fun nextId(): String = idCounter.getAndIncrement().toString()

    private fun toApiFile(inMemoryFile: InMemoryFile): File {
        return File().apply {
            id = inMemoryFile.id
            name = inMemoryFile.name
            mimeType = inMemoryFile.mimeType
        }
    }

    private fun ensureSize(rows: MutableList<MutableList<String>>, rowIndex: Int, colIndex: Int) {
        while (rows.size <= rowIndex) {
            rows.add(mutableListOf())
        }
        while (rows[rowIndex].size <= colIndex) {
            rows[rowIndex].add("")
        }
    }

    private fun parseRange(range: String): RangeCoordinates {
        val parts = range.split(":")
        val start = parseCell(parts[0])
        val end = if (parts.size > 1) parseCell(parts[1]) else start
        return RangeCoordinates(
            rowStart = start.row,
            colStart = start.col,
            rowEnd = end.row,
            colEnd = end.col
        )
    }


    private fun parseCell(cellRef: String): CellCoordinates {
        val ref = cellRef.trim().uppercase()

        val match = Regex("([A-Z]+)([0-9]+)").find(ref)
            ?: throw IllegalArgumentException("Invalid cell reference: $cellRef")

        val (letters, digits) = match.destructured
        val rowIndex = digits.toInt() - 1
        val colIndex = lettersToIndex(letters)

        return CellCoordinates(rowIndex, colIndex)
    }

    private fun lettersToIndex(letters: String): Int {
        var result = 0
        letters.forEach { ch ->
            result = result * 26 + (ch - 'A' + 1)
        }
        return result - 1
    }

    data class CellCoordinates(val row: Int, val col: Int)
    data class RangeCoordinates(val rowStart: Int, val colStart: Int, val rowEnd: Int, val colEnd: Int)

    companion object {
        private const val FOLDER_MIME = "application/vnd.google-apps.folder"
        private const val SPREADSHEET_MIME = "application/vnd.google-apps.spreadsheet"
    }
}
