package odyseja.odysejapka.timetable

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
class ImportCsvService(
    private val timeTableService: TimeTableService,
    private val cityRepository: CityRepository
) {
    fun uploadCsvFile(file: MultipartFile, cityId: Int): List<PerformanceEntity> {
        throwIfFileEmpty(file)

        val city: CityEntity
        try {
            city = cityRepository.findFirstById(cityId)
        } catch (e: Exception) {
            throw IllegalArgumentException("Nie ma konkursu o ID ${cityId}.")
        }

        return BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8)).use { reader ->
            val csvToBean = createCSVToBean(reader)
            val parsed = csvToBean.parse()
            timeTableService.addPerformance(parsed, city)
        }
    }

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty)
            throw RuntimeException("Empty file")
    }

    private fun createCSVToBean(fileReader: BufferedReader?): CsvToBean<Performance> {
        return CsvToBeanBuilder<Performance>(fileReader)
            .withType(Performance::class.java)
            .withIgnoreLeadingWhiteSpace(true)
            .withSkipLines(0)
            .build()
    }
}