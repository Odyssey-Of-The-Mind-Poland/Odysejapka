package odyseja.odysejapka.timetable

import odyseja.odysejapka.Progress
import odyseja.odysejapka.async.BackgroundJobService
import odyseja.odysejapka.city.CityRepository
import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.exceptions.CityNotFoundException
import org.springframework.stereotype.Service

@Service
class ImportTimetableService(
    private val performanceService: TimeTableService,
    private val cityRepository: CityRepository,
    private val backgroundJobService: BackgroundJobService
) {

    private val jobType = "timetable"

    fun import(zspId: String, cityId: Int) {
        clearTimeTable(cityId)
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val city = cityRepository.findFirstById(cityId) ?: throw CityNotFoundException(cityId)
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
