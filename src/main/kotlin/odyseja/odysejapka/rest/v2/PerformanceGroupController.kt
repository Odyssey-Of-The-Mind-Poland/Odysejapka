package odyseja.odysejapka.rest.v2

import odyseja.odysejapka.domain.PerformanceGroup
import odyseja.odysejapka.service.PerformanceGroupService
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
import java.io.File


@RestController()
@RequestMapping("/api/v2/timeTable")
class PerformanceGroupController(private val performanceGroupService: PerformanceGroupService) {

    @GetMapping
    fun getPerformanceGroups(@RequestParam(required = false) cityId: Int?): List<PerformanceGroup> {
        return performanceGroupService.getPerformanceGroups(cityId)
    }

    @GetMapping("/generate-excel")
    fun generateExcel(): ResponseEntity<ByteArray> {
        val filePath = "performances.xlsx"
        val file = File(filePath)

        if (!file.exists()) {
            file.createNewFile()
        }

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Performances")

        val performanceGroups = getPerformanceGroups(0)

        var currentRow = 0
        for (pg in performanceGroups) {
            val headerRow = sheet.createRow(currentRow++)
            headerRow.createCell(0).setCellValue("SCENA ${pg.group.stage} – ${pg.group.city}")
            currentRow++

            for (performance in pg.performances) {
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
}