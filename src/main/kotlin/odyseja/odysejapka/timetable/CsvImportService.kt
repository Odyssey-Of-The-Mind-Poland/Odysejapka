package odyseja.odysejapka.timetable

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.enums.CSVReaderNullFieldIndicator
import odyseja.odysejapka.stage.StageUserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
class CsvImportService(
    private val timeTableService: TimeTableService,
    private val stageUserService: StageUserService
) {
    @Transactional
    fun uploadCsvFile(file: MultipartFile, cityId: Int) {
        BufferedReader(InputStreamReader(file.inputStream, StandardCharsets.UTF_8)).use { reader ->
            val beans = createCSVToBean(reader)
            val parsed = beans.parse()

            if (parsed.isEmpty()) throw IllegalArgumentException("Plik nie zawiera żadnych przedstawień.")

            parsed.forEach {
                it.standardize()
                it.validate()
            }

            timeTableService.addPerformances(parsed, cityId)

            val stages = parsed.map { it.stage }.toSet()
            stageUserService.createStageUsers(cityId, stages)
        }
    }

    private fun createCSVToBean(fileReader: BufferedReader?): CsvToBean<Performance> {
        return CsvToBeanBuilder<Performance>(fileReader)
                .withType(Performance::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .withSkipLines(0)
                .withThrowExceptions(true)
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .build()
    }
}