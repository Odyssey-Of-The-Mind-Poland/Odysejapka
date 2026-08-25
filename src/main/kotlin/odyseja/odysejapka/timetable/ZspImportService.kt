package odyseja.odysejapka.timetable

import odyseja.odysejapka.Progress
import odyseja.odysejapka.async.BackgroundJobService
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.form.TeamResultService
import org.springframework.stereotype.Service

@Service
class ZspImportService(
    private val timeTableService: TimeTableService,
    private val cityService: CityService,
    private val backgroundJobService: BackgroundJobService,
    private val teamResultService: TeamResultService
) {

    private val jobType = "timetable"

    fun import(zspId: String, cityId: Int) {
        timeTableService.clearTimetableByCity(cityId)
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val city = cityService.getCity(cityId)

        backgroundJobService.start(
            jobType,
            TimeTableRunner(timeTableService, sheetsAdapter, city.name)
        )
    }

    fun clearTimeTable(cityId: Int) {
        performanceService.deleteByCity(cityId)
    }

    fun stop() {
        backgroundJobService.stop(jobType)
    }

    fun getProgress(): Progress {
        return backgroundJobService.getProgress(jobType)
    }
}
