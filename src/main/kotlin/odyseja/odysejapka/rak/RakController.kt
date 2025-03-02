package odyseja.odysejapka.rak

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.io.ByteArrayOutputStream

data class ZspIdRequest(
    val zspId: String
)

@RestController
@RequestMapping("/api/v1/rak")
class RakController(
    private val rakService: RakService,
    private val templateEngine: TemplateEngine
) {
    @PostMapping("/generate")
    fun generateCsv(@RequestBody request: ZspIdRequest): ResponseEntity<ByteArray> {
        val csvData = rakService.generateCsv(request.zspId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=your_file.csv")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(csvData)
    }

    @PostMapping("/download-html")
    fun downloadHtmlResults(@RequestBody request: ZspIdRequest): ResponseEntity<String> {
        val renderedHtml = generateHtmlResults(request)

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(renderedHtml)
    }

    @PostMapping("/download-pdf")
    fun downloadPdf(@RequestBody request: ZspIdRequest): ResponseEntity<ByteArray> {
        val renderedHtml = generateHtmlResults(request)
        val pdfBytes = convertHtmlToPdf(renderedHtml)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    private fun generateHtmlResults(request: ZspIdRequest): String {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(request.zspId)
        val groups: List<FinalScoreGroup> = RakCalculator().calculateScores(sheetsAdapter.getAllTeams())

        val context = Context().apply {
            setVariable("groups", groups)
        }
        return templateEngine.process("results.html", context)
    }

    fun convertHtmlToPdf(html: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val builder = PdfRendererBuilder()

        builder.withHtmlContent(html, null)
        builder.toStream(outputStream)

        builder.run()

        return outputStream.toByteArray()
    }

}