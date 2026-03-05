package odyseja.odysejapka.fixer

import odyseja.odysejapka.async.AsyncLogger
import odyseja.odysejapka.async.CancellableRunner
import odyseja.odysejapka.async.Log
import com.google.api.services.drive.model.File
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class FixerRunner(
    private val driveAdapter: DriveAdapter,
    private val sheetsAdapter: SheetAdapter,
    private val folderId: String,
    private val pattern: String,
    private val cells: List<FixSheetsCommand.ReplacementCell>
) : CancellableRunner {

    companion object {
        private const val MAX_DEPTH = 5
        private const val FOLDER_MIME = "application/vnd.google-apps.folder"
    }

    private val logger = AsyncLogger()
    private val cancelled = AtomicBoolean(false)
    private var totalFileCount = 0
    private val processedFileCount = AtomicInteger(0)

    override fun run() {
        val allFiles = collectFiles(folderId, 0)
        totalFileCount = allFiles.size.coerceAtLeast(1)
        for (file in allFiles) {
            if (cancelled.get()) return
            try {
                logger.log("fixSheet: ${file.name}")
                fixSheet(file.id)
            } catch (e: Exception) {
                logger.error("Error fixing sheet ${file.name}: ${e.message ?: e}")
            }
            processedFileCount.incrementAndGet()
        }
        logger.log("finished fixing sheets")
    }

    override fun requestCancel() {
        cancelled.set(true)
    }

    override fun isCancelled(): Boolean = cancelled.get()

    private fun fixSheet(id: String) {
        for (cell in cells) {
            if (cancelled.get()) return
            sheetsAdapter.writeValue(id, cell.sheetName, cell.cell, cell.value)
            Thread.sleep(500)
        }
    }

    private fun collectFiles(parentId: String, depth: Int): List<File> {
        if (depth >= MAX_DEPTH) return emptyList()
        val children = driveAdapter.listFiles(parentId)
        val matched = children.filter { it.mimeType != FOLDER_MIME && matchFileName(it.name, pattern) }
        val nested = children.filter { it.mimeType == FOLDER_MIME }
            .flatMap { collectFiles(it.id, depth + 1) }
        return matched + nested
    }

    private fun matchFileName(name: String?, pattern: String): Boolean {
        val regexPattern = pattern.replace("*", ".*").toRegex()
        return name?.matches(regexPattern) ?: false
    }

    override fun getProgress(): Int {
        return (processedFileCount.get() * 100) / totalFileCount
    }

    override fun getLogs(): List<Log> {
        return logger.getLogs()
    }
}