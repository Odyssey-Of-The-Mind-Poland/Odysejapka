package odyseja.odysejapka.timetable

import com.google.api.services.sheets.v4.model.Sheet
import odyseja.odysejapka.async.AsyncLogger
import odyseja.odysejapka.async.CancellableRunner
import odyseja.odysejapka.async.Log
import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.gad.Teams
import odyseja.odysejapka.timetable.TimeTableService
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class TimeTableRunner(
    private val timeTableService: TimeTableService,
    private val sheetsAdapter: ZspSheetsAdapter,
    private val city: String
) : CancellableRunner {

    private val logger = AsyncLogger()
    private val cancelled = AtomicBoolean(false)
    private var totalSheetCount = 1
    private val processedSheetCount = AtomicInteger(0)

    override fun run() {
        val sheets = sheetsAdapter.getSheets()
        totalSheetCount = sheets?.size ?: 1
        for (sheet in sheets!!) {
            if (cancelled.get()) return
            try {
                processSheet(sheet)
            } catch (e: Exception) {
                logger.error("Error processing sheet ${sheet.properties.title}: ${e.message ?: e}")
            }
            processedSheetCount.incrementAndGet()
        }
    }

    override fun requestCancel() {
        cancelled.set(true)
    }

    override fun isCancelled(): Boolean = cancelled.get()

    override fun getProgress(): Int {
        return (processedSheetCount.get() * 100) / totalSheetCount
    }

    override fun getLogs(): List<Log> {
        return logger.getLogs()
    }

    private fun processSheet(sheet: Sheet) {
        val title = sheet.properties.title
        logger.log("Processing: $title")

        val teams = sheetsAdapter.getTeams(title)

        processTeams(teams)
    }

    private fun processTeams(teams: Teams) {
        for (team in teams.teams) {
            if (cancelled.get()) return
            try {
                timeTableService.addPerformance(team.toPerformance(city))
            } catch (e: Exception) {
                logger.error("Error adding performance for team ${team.teamName}: ${e.message ?: e}")
            }
        }
    }
}
