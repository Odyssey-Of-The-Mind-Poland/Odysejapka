package odyseja.odysejapka.form

import odyseja.odysejapka.dashboard.PerformanceAccessService
import odyseja.odysejapka.dashboard.UserAccessService
import odyseja.odysejapka.dashboard.extractUserId
import odyseja.odysejapka.timetable.PerformanceRepository
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
    private val performanceAccessService: PerformanceAccessService,
    private val userAccessService: UserAccessService,
    private val performanceRepository: PerformanceRepository
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

    @GetMapping("/subjective-ranges")
    fun getSubjectiveRanges(): List<SubjectiveRangeDto> {
        return SubjectiveRanges.entries.map { it.toRangesResponse() }
    }

    @GetMapping("/objective-buckets")
    fun getObjectiveBuckets(): List<ObjectiveBucketDto> {
        return ObjectiveBuckets.entries.map { it.toBucketsResponse() }
    }

    @PutMapping("/{performanceId}/approve")
    fun approveForm(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?
    ) {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)
        val performance = performanceRepository.findById(performanceId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val problem = performance.problemEntity.id
        if (!userAccessService.isAdmin() && !userAccessService.isKapitanForProblem(problem)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        formService!!.approveForm(performanceId)
    }

    @GetMapping("/{performanceId}/preview/pdf")
    fun getTeamFormPdf(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?
    ): ResponseEntity<ByteArray> {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)
        val teamForm = formService!!.getTeamForm(performanceId)
        if (!teamForm.approved) throw ResponseStatusException(HttpStatus.CONFLICT, "Form not approved")
        val pdfBytes = teamFormPdfGeneratorService!!.generatePdf(performanceId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"team-form-${performanceId}.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @GetMapping("/{performanceId}/preview/pdf/english")
    fun getTeamFormPdfEnglish(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?
    ): ResponseEntity<ByteArray> {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)
        val teamForm = formService!!.getTeamForm(performanceId)
        if (!teamForm.approved) throw ResponseStatusException(HttpStatus.CONFLICT, "Form not approved")
        val pdfBytes = teamFormPdfGeneratorService!!.generatePdf(performanceId, english = true)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"team-form-${performanceId}-en.pdf\"")
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
        val teamForm = formService!!.getTeamForm(performanceId)
        if (!teamForm.approved) throw ResponseStatusException(HttpStatus.CONFLICT, "Form not approved")
        val pdfBytes = teamFormPdfGeneratorService!!.generatePdf(performanceId)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"team-form-${performanceId}.pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }
}