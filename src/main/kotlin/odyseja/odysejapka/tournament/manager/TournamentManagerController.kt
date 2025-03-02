package odyseja.odysejapka.tournament.manager

import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

data class ZspIdRequest(
    val zspId: String
)

@RestController
@RequestMapping("/api/v1/tm")
class TournamentManagerController(
    private val tournamentManagerService: TournamentManagerService,
    private val templateEngine: TemplateEngine
) {

    @PostMapping("/generate")
    fun generateCsv(@RequestBody request: ZspIdRequest): ResponseEntity<ByteArray> {
        val csvData = tournamentManagerService.generateCsv(request.zspId)

        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=your_file.csv")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(csvData)
    }

    @PostMapping("/download-html")
    fun downloadHtmlResults(@RequestBody request: ZspIdRequest): ResponseEntity<String> {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(request.zspId)
        val groups: List<FinalScoreGroup> = TMCalculator().calculateScores(sheetsAdapter.getAllTeams())

        val context = Context().apply {
            setVariable("groups", groups)
        }

        val renderedHtml = templateEngine.process("results.html", context)

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(renderedHtml)
    }
}