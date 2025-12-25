package odyseja.odysejapka.timetable

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Service
class ImportCsvService(
    private val timeTableService: TimeTableService
) {
    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty)
            throw RuntimeException("Empty file")
    }

    private fun createCSVToBean(fileReader: BufferedReader?): CsvToBean<Performance> {
        return CsvToBeanBuilder<Performance>(fileReader)
            .withType(Performance::class.java)
            .withIgnoreLeadingWhiteSpace(true)
            .withSkipLines(1)
            .build()
    }

    private fun closeFileReader(fileReader: BufferedReader?) {
        try {
            fileReader!!.close()
        } catch (ex: IOException) {
            throw RuntimeException("Error during csv import: ${ex.message}.")
        }
    }

    fun uploadCsvFile(file: MultipartFile): List<PerformanceEntity> {
        throwIfFileEmpty(file)
        var fileReader: BufferedReader? = null

        try {
            print("File received")
            fileReader = BufferedReader(InputStreamReader(file.inputStream))
            val csvToBean = createCSVToBean(fileReader)

            return timeTableService.addPerformance(csvToBean.parse())
        } catch (ex: Exception) {
            throw RuntimeException("Error during csv import: ${ex}.")
        } finally {
            closeFileReader(fileReader)
        }
    }
}