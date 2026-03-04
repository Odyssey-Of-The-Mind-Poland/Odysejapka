package odyseja.odysejapka.fixer

import odyseja.odysejapka.async.AsyncLogger
import odyseja.odysejapka.async.CancellableRunner
import odyseja.odysejapka.async.Log
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

    private val logger = AsyncLogger()
    private val cancelled = AtomicBoolean(false)
    private var totalFileCount = 0
    private val processedFileCount = AtomicInteger(0)

    override fun run() {
        val allFiles = driveAdapter.listFiles(folderId).flatMap { folder ->
            driveAdapter.listFiles(folder.id).filter { matchFileName(it.name, pattern) }
        }
        totalFileCount = allFiles.size.coerceAtLeast(1)
        for (file in allFiles) {
            if (cancelled.get()) return
            logger.log("fixSheet: ${file.name}")
            fixSheet(file.id)
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