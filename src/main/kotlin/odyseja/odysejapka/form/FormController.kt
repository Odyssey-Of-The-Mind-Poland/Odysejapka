package odyseja.odysejapka.form

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/form")
class FormController(
    private val formService: FormService?,
    private val teamFormHtmlGeneratorService: TeamFormHtmlGeneratorService?,
    private val teamFormPdfGeneratorService: TeamFormPdfGeneratorService?
) {

    @PutMapping("/{problem}")
    fun setProblemForm(@PathVariable problem: Int, @RequestBody problemForm: ProblemForm) {
        formService!!.setFormEntries(problem, problemForm)
    }

    @GetMapping("/{problem}")
    fun getProblemForm(@PathVariable problem: Int): ProblemForm {
        return formService!!.getProblemForm(problem)
    }

    @GetMapping("/{problem}/judge-count")
    fun getJudgeCount(@PathVariable problem: Int, @RequestParam cityId: Int): JudgeCountResponse {
        return formService!!.getJudgeCount(problem, cityId)
    }

    @PutMapping("/{performanceId}/result")
    fun setTeamResults(@PathVariable performanceId: Int, @RequestBody result: PerformanceResultsRequest) {
        formService!!.setTeamResults(performanceId, result)
    }

    @GetMapping("/{performanceId}/result")
    fun getTeamResults(@PathVariable performanceId: Int): TeamForm {
        return formService!!.getTeamForm(performanceId)
    }

    @GetMapping("/subjective-ranges")
    fun getSubjectiveRanges(): List<SubjectiveRangeDto> {
        return SubjectiveRanges.entries.map { it.toRangesResponse() }
    }

    @GetMapping("/objective-buckets")
    fun getObjectiveBuckets(): List<ObjectiveBucketDto> {
        return ObjectiveBuckets.entries.map { it.toBucketsResponse() }
    }

    @GetMapping("/{performanceId}/preview")
    fun getTeamFormPreview(@PathVariable performanceId: Int): ResponseEntity<String> {
        val html = teamFormHtmlGeneratorService!!.generateHtml(performanceId)
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(html)
    }

    @GetMapping("/{performanceId}/preview/pdf")
    fun getTeamFormPdf(@PathVariable performanceId: Int): ResponseEntity<ByteArray> {
        val pdfBytes = teamFormPdfGeneratorService!!.generatePdf(performanceId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"team-form-${performanceId}.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @GetMapping("/{performanceId}/preview/pdf/download")
    fun downloadTeamFormPdf(@PathVariable performanceId: Int): ResponseEntity<ByteArray> {
        val pdfBytes = teamFormPdfGeneratorService!!.generatePdf(performanceId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"team-form-${performanceId}.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }
}