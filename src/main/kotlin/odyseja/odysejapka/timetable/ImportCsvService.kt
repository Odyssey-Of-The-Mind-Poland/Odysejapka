package odyseja.odysejapka.timetable

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
class ImportCsvService(
    private val timeTableService: TimeTableService
) {
    fun uploadCsvFile(file: MultipartFile, city: String): List<PerformanceEntity> {
        throwIfFileEmpty(file)

        return BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8)).use { reader ->
            val csvToBean = createCSVToBean(reader)
            val parsed = csvToBean.parse()

            println("OpenCSV Exceptions: ${csvToBean.capturedExceptions}")

            if (parsed.isNotEmpty()) {
                println("First parsed row: ${parsed[0]}")
            }

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