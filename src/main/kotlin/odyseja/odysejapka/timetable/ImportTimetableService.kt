package odyseja.odysejapka.timetable

import TimeTableImporter
import com.google.api.client.json.gson.GsonFactory
import odyseja.odysejapka.drive.CredentialsProvider
import odyseja.odysejapka.drive.SpreadSheetsAdapter
import odyseja.odysejapka.service.CityRepository
import org.springframework.stereotype.Service

@Service
class ImportTimetableService(
    private val performanceService: TimeTableService,
    private val cityRepository: CityRepository
) {

    fun import(zspId: String, cityId: Int) {
        clearTimeTable(cityId)
        val credentials = CredentialsProvider().getCredentials()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val sheetsAdapter = SpreadSheetsAdapter(credentials, jsonFactory, zspId)
        val cityName = cityRepository.findFirstById(cityId).name
        val timeTableImporter = TimeTableImporter(performanceService, sheetsAdapter, cityName)
        timeTableImporter.startImporting()
    }

    fun clearTimeTable(cityId: Int) {
        performanceService.deleteCity(cityId)
    }
}
