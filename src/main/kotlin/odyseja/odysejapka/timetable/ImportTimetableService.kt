package odyseja.odysejapka.timetable

import odyseja.odysejapka.Progress
import odyseja.odysejapka.async.BackgroundJobService
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service

@Service
class ImportTimetableService(
    private val performanceService: TimeTableService,
    private val cityService: CityService,
    private val backgroundJobService: BackgroundJobService
) {

    private val jobType = "timetable"

    fun import(zspId: String, cityId: Int) {
        clearTimeTable(cityId)
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val city = cityService.getCity(cityId)
        performanceService.deleteByCity(cityId)

        backgroundJobService.start(
            jobType,
            TimeTableRunner(performanceService, sheetsAdapter, city.name)
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
