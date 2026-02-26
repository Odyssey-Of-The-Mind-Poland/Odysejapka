package odyseja.odysejapka.form

import odyseja.odysejapka.dashboard.PerformanceAccessService
import odyseja.odysejapka.dashboard.extractUserId
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/form")
class FormController(
    private val formService: FormService?,
    private val teamFormPdfGeneratorService: TeamFormPdfGeneratorService?,
    private val performanceAccessService: PerformanceAccessService
) {

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PutMapping("/{problem}")
    fun setProblemForm(@PathVariable problem: Int, @RequestBody problemForm: ProblemForm) {
        formService!!.setFormEntries(problem, problemForm)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @GetMapping("/{problem}")
    fun getProblemForm(@PathVariable problem: Int): ProblemForm {
        return formService!!.getProblemForm(problem)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @GetMapping("/{problem}/judge-count")
    fun getJudgeCount(@PathVariable problem: Int, @RequestParam cityId: Int): JudgeCountResponse {
        return formService!!.getJudgeCount(problem, cityId)
    }

    @PutMapping("/{performanceId}/result")
    fun setTeamResults(
        @PathVariable performanceId: Int,
        @RequestBody result: PerformanceResultsRequest,
        @AuthenticationPrincipal principal: Any?
    ) {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)
        formService!!.setTeamResults(performanceId, result)
    }

    @GetMapping("/{performanceId}/result")
    fun getTeamResults(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?
    ): TeamForm {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)
        return formService!!.getTeamForm(performanceId)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @GetMapping("/subjective-ranges")
    fun getSubjectiveRanges(): List<SubjectiveRangeDto> {
        return SubjectiveRanges.entries.map { it.toRangesResponse() }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @GetMapping("/objective-buckets")
    fun getObjectiveBuckets(): List<ObjectiveBucketDto> {
        return ObjectiveBuckets.entries.map { it.toBucketsResponse() }
    }

    @GetMapping("/{performanceId}/preview/pdf")
    fun getTeamFormPdf(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?
    ): ResponseEntity<ByteArray> {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)
        val pdfBytes = teamFormPdfGeneratorService!!.generatePdf(performanceId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"team-form-${performanceId}.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @GetMapping("/{performanceId}/preview/pdf/download")
    fun downloadTeamFormPdf(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?
    ): ResponseEntity<ByteArray> {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)
        val pdfBytes = teamFormPdfGeneratorService!!.generatePdf(performanceId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"team-form-${performanceId}.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }
}