package odyseja.odysejapka.fixer

import odyseja.odysejapka.async.Log
import odyseja.odysejapka.async.Runner
import odyseja.odysejapka.drive.DriveAdapter
import odyseja.odysejapka.drive.SheetAdapter
import java.lang.Thread.sleep

class FixerRunner(
    private val driveAdapter: DriveAdapter,
    private val sheetsAdapter: SheetAdapter,
    private val folderId: String,
    private val pattern: String,
    private val cells: List<FixSheetsCommand.ReplacementCell>
) : Runner {
    override fun run() {
        driveAdapter.listFiles(folderId).forEach {
            driveAdapter.listFiles(it.id).forEach { file ->
                if (matchFileName(file.name, pattern)) {
                    println("fixSheet: ${file.name}")
                    fixSheet(file.id)
                }
            }
        }
        println("finished fixing sheets")
    }

    private fun fixSheet(id: String) {
        for (cell in cells) {
            sheetsAdapter.writeValue(id, cell.sheetName, cell.cell, cell.value)
            sleep(500)
        }
    }

    private fun matchFileName(name: String?, pattern: String): Boolean {
        val regexPattern = pattern.replace("*", ".*").toRegex()
        return name?.matches(regexPattern) ?: false
    }

    override fun getProgress(): Int {
        TODO("Not yet implemented")
    }

    override fun getLogs(): List<Log> {
        TODO("Not yet implemented")
    }
}