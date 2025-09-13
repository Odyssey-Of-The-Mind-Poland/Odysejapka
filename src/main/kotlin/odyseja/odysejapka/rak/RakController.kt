package odyseja.odysejapka.rak

import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.rak.PdfGeneratorService.Companion.RESULTS_TEMPLATE
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class ZspIdRequest(
    val zspId: String
)

@RestController
@RequestMapping("/api/v1/rak")
class RakController(
    private val rakService: TmCsvService,
    private val csvService: CsvService,
    private val pdfGeneratorService: PdfGeneratorService,
    private val mockedPdfService: MockedPdfService,
    private val templateStoreService: TemplateStoreService
) {
    @PostMapping("/generate")
    fun generateCsv(@RequestBody request: ZspIdRequest): ResponseEntity<ByteArray> {
        val csvData = rakService.generateCsv(request.zspId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=your_file.csv")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(csvData)
    }

    @PostMapping("/generate-detailed")
    fun generateDetailedCsv(@RequestBody request: ZspIdRequest): ResponseEntity<ByteArray> {
        val csvData = csvService.generateCsv(request.zspId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=detailed_results.csv")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(csvData)
    }

    @PostMapping("/download-pdf")
    fun downloadPdf(
        @RequestBody request: ZspIdRequest
    ): ResponseEntity<ByteArray> {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(request.zspId)
        val pdfBytes = pdfGeneratorService.generatePdf(sheetsAdapter.getAllTeams())
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @GetMapping("/pdf-template")
    fun getPdfTemplate(): ResponseEntity<String> {
        val template = templateStoreService.find(RESULTS_TEMPLATE)?.content ?: "Template not found"
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(template)
    }

    @PostMapping(
        "/pdf-template",
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun setPdfTemplate(@RequestBody body: String): ResponseEntity<String> {
        val saved = templateStoreService.update(RESULTS_TEMPLATE, body).content
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(saved)
    }


    @PostMapping("/download-short-pdf")
    fun downloadShortPdf(
        @RequestBody request: ZspIdRequest
    ): ResponseEntity<ByteArray> {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(request.zspId)
        val pdfBytes = pdfGeneratorService.generateShortPdf(sheetsAdapter.getAllTeams())
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results-short.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @PostMapping("/download-mocked-pdf")
    fun downloadMockedPdf(): ResponseEntity<ByteArray> {
        val pdfBytes = mockedPdfService.generatePdf()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }


}