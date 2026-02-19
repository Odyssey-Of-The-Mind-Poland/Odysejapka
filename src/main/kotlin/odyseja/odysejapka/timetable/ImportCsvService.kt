package odyseja.odysejapka.timetable

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
class ImportCsvService(
    private val timeTableService: TimeTableService,
    private val cityRepository: CityRepository
) {
    @Transactional
    fun uploadCsvFile(file: MultipartFile, cityId: Int) {
        throwIfFileEmpty(file)

        val city: CityEntity
        try {
            city = cityRepository.findFirstById(cityId)
        } catch (e: Exception) {
            throw IllegalArgumentException("Nie ma konkursu o ID ${cityId}.")
        }

        BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8)).use { reader ->
            val beans = createCSVToBean(reader)
            val parsed = beans.parse()

            parsed.forEach {
                it.standardize()
                it.validate()
            }

            timeTableService.addPerformances(parsed, city)
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
            .withThrowExceptions(false)
            .build()
    }
}