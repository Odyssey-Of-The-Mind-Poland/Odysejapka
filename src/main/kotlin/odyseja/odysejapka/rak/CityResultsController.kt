package odyseja.odysejapka.rak

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/dashboard/cities/{cityId}/results")
class CityResultsController(
    private val cityResultsService: CityResultsService
) {

    @GetMapping("/status")
    @Secured("ROLE_ADMINISTRATOR")
    fun getResultsStatus(@PathVariable cityId: Int): ResultsStatusResponse {
        return cityResultsService.getResultsStatus(cityId)
    }

    @PostMapping("/pdf")
    @Secured("ROLE_ADMINISTRATOR")
    fun generateResultsPdf(@PathVariable cityId: Int): ResponseEntity<ByteArray> {
        val pdf = cityResultsService.generateResultsPdf(cityId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wyniki.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf)
    }
}
