package odyseja.odysejapka.rak

import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.util.GoogleIdExtractor
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


data class ZspIdRequest(
    val zspId: String,
    val contestName: String? = null
)

@RestController
@RequestMapping("/api/v1/rak")
class RakController(
    private val rakService: TmCsvService,
    private val csvService: CsvService,
    private val htmlGeneratorService: HtmlGeneratorService,
    private val pdfGeneratorService: PdfGeneratorService,
    private val latexGeneratorService: LatexGeneratorService,
    private val mockedPdfService: MockedPdfService,
    private val rakCommandService: RakCommandService
) {
    @PostMapping("/generate")
    fun generateCsv(
        @RequestBody request: ZspIdRequest,
        @RequestParam(required = false) cityId: Int?
    ): ResponseEntity<ByteArray> {
        val zspId = GoogleIdExtractor.extractGoogleId(request.zspId)
        rakCommandService.saveCommand(zspId, request.contestName, cityId)
        val csvData = rakService.generateCsv(zspId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=your_file.csv")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(csvData)
    }

    @PostMapping("/generate-detailed")
    fun generateDetailedCsv(
        @RequestBody request: ZspIdRequest,
        @RequestParam(required = false) cityId: Int?
    ): ResponseEntity<ByteArray> {
        val zspId = GoogleIdExtractor.extractGoogleId(request.zspId)
        rakCommandService.saveCommand(zspId, request.contestName, cityId)
        val csvData = csvService.generateCsv(zspId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=detailed_results.csv")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(csvData)
    }

    @PostMapping("/download-html")
    fun downloadHtmlResults(
        @RequestBody request: ZspIdRequest,
        @RequestParam(required = false) cityId: Int?,
        @RequestParam(required = false, defaultValue = "false") isRegion: Boolean
    ): ResponseEntity<String> {
        val zspId = GoogleIdExtractor.extractGoogleId(request.zspId)
        rakCommandService.saveCommand(zspId, request.contestName, cityId)
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val renderedHtml = htmlGeneratorService.generateHtmlResults(sheetsAdapter.getAllTeams(), isRegion, request.contestName)

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(renderedHtml)
    }

    @PostMapping("/download-pdf")
    fun downloadPdf(
        @RequestBody request: ZspIdRequest,
        @RequestParam(required = false) cityId: Int?,
        @RequestParam(required = false, defaultValue = "false") isRegion: Boolean
    ): ResponseEntity<ByteArray> {
        val zspId = GoogleIdExtractor.extractGoogleId(request.zspId)
        rakCommandService.saveCommand(zspId, request.contestName, cityId)
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val pdfBytes = pdfGeneratorService.generatePdf(sheetsAdapter.getAllTeams(), isRegion, request.contestName)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @PostMapping("/download-short-pdf")
    fun downloadShortPdf(
        @RequestBody request: ZspIdRequest,
        @RequestParam(required = false) cityId: Int?,
        @RequestParam(required = false, defaultValue = "false") isRegion: Boolean
    ): ResponseEntity<ByteArray> {
        val zspId = GoogleIdExtractor.extractGoogleId(request.zspId)
        rakCommandService.saveCommand(zspId, request.contestName, cityId)
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val pdfBytes = pdfGeneratorService.generateShortPdf(sheetsAdapter.getAllTeams(), isRegion, request.contestName)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results-short.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @PostMapping("/download-latex-pdf")
    fun downloadLatexPdf(
        @RequestBody request: ZspIdRequest,
        @RequestParam(required = false) cityId: Int?,
        @RequestParam(required = false, defaultValue = "false") isRegion: Boolean
    ): ResponseEntity<ByteArray> {
        val zspId = GoogleIdExtractor.extractGoogleId(request.zspId)
        rakCommandService.saveCommand(zspId, request.contestName, cityId)
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val pdfBytes = latexGeneratorService.generatePdf(sheetsAdapter.getAllTeams(), isRegion, request.contestName)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results-latex.pdf\"")
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

    @GetMapping
    fun getLastZspId(@RequestParam(required = false) cityId: Int?): ZspIdRequest {
        return rakCommandService.getLastCommand(cityId)
    }


}