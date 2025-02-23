package odyseja.odysejapka.timetable

import TimeTableImporter
import odyseja.odysejapka.Progress
import odyseja.odysejapka.Status
import odyseja.odysejapka.city.CityRepository
import odyseja.odysejapka.drive.SheetAdapter
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service

@Service
class ImportTimetableService(
    private val performanceService: TimeTableService,
    private val cityRepository: CityRepository,
    private val sheetsAdapter: SheetAdapter
) {

    private var importer: TimeTableImporter? = null
    private var job: Thread? = null

    fun import(zspId: String, cityId: Int) {

        if (importer != null || job?.isAlive == true) {
            throw RuntimeException("Gad is already running")
        }

        clearTimeTable(cityId)
        val sheetsAdapter = ZspSheetsAdapter(sheetsAdapter, zspId)
        val cityName = cityRepository.findFirstById(cityId).name
        performanceService.deleteCity(cityId)
        importer = TimeTableImporter(performanceService, sheetsAdapter, cityName)

        job = Thread {
            importer?.startImporting()
        }
        job?.start()
    }

    fun clearTimeTable(cityId: Int) {
        performanceService.deleteCity(cityId)
    }

    fun stop() {
        job?.stop()
        importer = null
    }

    fun getProgress(): Progress {
        val progress = importer?.getProgress() ?: 100
        return if (progress != 100) {
            Progress(progress, Status.RUNNING)
        } else {
            Progress(100, Status.STOPPED)
        }
    }
}
