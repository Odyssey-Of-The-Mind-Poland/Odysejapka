package odyseja.odysejapka.rest.v2

import odyseja.odysejapka.domain.Performance
import odyseja.odysejapka.domain.PerformanceGroup
import odyseja.odysejapka.service.PerformanceGroupService
import odyseja.odysejapka.service.TimeTableService
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream


@RestController()
@RequestMapping("/api/v2/timeTable")
class PerformanceGroupController(
    private val performanceGroupService: PerformanceGroupService,
    private val timeTableService: TimeTableService
) {

    @GetMapping
    fun getPerformanceGroups(@RequestParam(required = false) cityId: Int?): List<PerformanceGroup> {
        return performanceGroupService.getPerformanceGroups(cityId)
    }

    @GetMapping("/generate-excel")
    fun generateExcel(): ResponseEntity<ByteArray> {

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Performances")

        val headerCellStyle = workbook.createCellStyle()
        val headerFont = workbook.createFont()
        headerFont.bold = true
        headerCellStyle.setFont(headerFont)
        headerCellStyle.fillForegroundColor = IndexedColors.YELLOW.getIndex()
        headerCellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        val stageGroupCellStyle = workbook.createCellStyle()
        val stageGroupFont = workbook.createFont()
        stageGroupFont.bold = true
        stageGroupCellStyle.setFont(stageGroupFont)


        val performanceGroups = timeTableService.getByCity(0).filter { it.performanceDay == "Sobota" }
            .sortedBy { it.performance }
            .groupBy { it.stage }.map { (stage, performances) ->
                PerformanceStage(
                    stageNumber = stage,
                    performances = performances
                )
            }
            .sortedBy { it.stageNumber }

        var currentRow = 0
        for (pg in performanceGroups) {
            val headerRow = sheet.createRow(currentRow++)
            val headerCell = headerRow.createCell(1)
            headerCell.setCellValue("SCENA ${pg.stageNumber}")
            headerCell.cellStyle = headerCellStyle

            var stageGroup: StageGroup? = null
            for (performance in pg.performances) {

                if (!performance.toStageGroup().equals(stageGroup)) {
                    stageGroup = performance.toStageGroup()
                    val row = sheet.createRow(currentRow++)
                    val cell = row.createCell(1)
                    cell.setCellValue(stageGroup.getName())
                    cell.cellStyle = stageGroupCellStyle
                }

                val row = sheet.createRow(currentRow++)
                row.createCell(0).setCellValue(performance.performance)
                row.createCell(1).setCellValue(performance.team)
                row.createCell(2).setCellValue(performance.spontan)
            }

            currentRow++
        }

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)

        val headers = HttpHeaders()
        headers.contentType =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        headers.contentDisposition = ContentDisposition.builder("attachment").filename("performances.xlsx").build()

        return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray())
    }

    data class PerformanceStage(val stageNumber: Int, val performances: List<Performance>)

    data class StageGroup(val problem: Int, val age: Int) {
        fun getName(): String {
            return "Problem $problem, grupa wiekowa $age"
        }
    }
}