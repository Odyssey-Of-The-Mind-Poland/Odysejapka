package odyseja.odysejapka.tournamentManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
data class ZspIdRequest(
        val zspId: String
)
@RestController
@RequestMapping("/api/v1/tm")
class TournamentManagerController(private val tournamentManagerService: TournamentManagerService) {
    @PostMapping("/generate")
    fun generateCsv(@RequestBody request: ZspIdRequest): ResponseEntity<ByteArray> {
        val csvData = tournamentManagerService.generateCsv(request.zspId)

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=your_file.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(csvData)
    }
}