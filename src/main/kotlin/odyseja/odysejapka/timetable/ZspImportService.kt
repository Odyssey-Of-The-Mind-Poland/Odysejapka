package odyseja.odysejapka.timetable

import odyseja.odysejapka.Progress
import odyseja.odysejapka.async.BackgroundJobService
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service

@Service
class ZspImportService(
    private val timeTableService: TimeTableService,
    private val backgroundJobService: BackgroundJobService,
) {

    private val jobType = "timetable"

    fun import(zspId: String, cityId: Int) {
        timeTableService.clearTimetableByCity(cityId)
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)

        backgroundJobService.start(
            jobType,
            TimeTableRunner(timeTableService, sheetsAdapter, cityId)
        )
    }

    fun stop() {
        backgroundJobService.stop(jobType)
    }

    fun getProgress(): Progress {
        return backgroundJobService.getProgress(jobType)
    }
}
