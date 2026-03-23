package odyseja.odysejapka.spontan

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

data class CreateSpontanUserRequest(val username: String)
data class AssignSpontanUserRequest(val spontanUserId: Long?)

@RestController
@RequestMapping("/api/v1/spontan")
class SpontanController(
    private val spontanService: SpontanService,
    private val spontanResultService: SpontanResultService,
    private val spontanUserService: SpontanUserService,
    private val spontanAccessService: SpontanAccessService
) {

    @GetMapping
    fun getAll(): List<SpontanDefinition> {
        return spontanService.getAllSpontans()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): SpontanDefinition {
        return spontanService.getSpontanById(id)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PostMapping
    fun create(@RequestBody definition: SpontanDefinition): SpontanDefinition {
        return spontanService.createSpontan(definition)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody definition: SpontanDefinition): SpontanDefinition {
        return spontanService.updateSpontan(id, definition)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        spontanService.deleteSpontan(id)
    }

    @GetMapping("/group/{cityId}")
    fun getGroupAssignments(@PathVariable cityId: Int): List<SpontanGroupAssignment> {
        return spontanService.getGroupAssignments(cityId)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PutMapping("/group/{cityId}/assign")
    fun assignSpontan(
        @PathVariable cityId: Int,
        @RequestParam problem: Int,
        @RequestParam age: Int,
        @RequestParam(required = false, defaultValue = "") league: String,
        @RequestBody request: AssignSpontanRequest
    ): SpontanGroupAssignment {
        val groupId = GroupId(problem, age, league)
        return spontanService.assignSpontan(cityId, groupId, request.spontanDefinitionId)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PutMapping("/group/{cityId}/judges")
    fun setJudgeCount(
        @PathVariable cityId: Int,
        @RequestParam problem: Int,
        @RequestParam age: Int,
        @RequestParam(required = false, defaultValue = "") league: String,
        @RequestBody request: SetJudgeCountRequest
    ): SpontanGroupAssignment {
        val groupId = GroupId(problem, age, league)
        return spontanService.setJudgeCount(cityId, groupId, request.judgeCount)
    }

    @GetMapping("/result/{cityId}/teams")
    fun getGroupTeams(
        @PathVariable cityId: Int,
        @RequestParam problem: Int,
        @RequestParam age: Int,
        @RequestParam(required = false, defaultValue = "") league: String
    ): SpontanGroupTeams {
        val groupId = GroupId(problem, age, league)
        spontanAccessService.verifyGroupAccess(cityId, groupId)
        return spontanResultService.getGroupTeams(cityId, groupId)
    }

    @PutMapping("/result/{performanceId}")
    fun setResults(
        @PathVariable performanceId: Int,
        @RequestBody request: SpontanResultsRequest
    ): SpontanTeamResult {
        spontanAccessService.verifyPerformanceAccess(performanceId)
        return spontanResultService.setResults(performanceId, request)
    }

    // Spontan user management endpoints

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PostMapping("/user/{cityId}")
    fun createSpontanUser(
        @PathVariable cityId: Int,
        @RequestBody request: CreateSpontanUserRequest
    ): SpontanUserCredentials {
        return spontanUserService.createSpontanUser(cityId, request.username)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @GetMapping("/user/{cityId}")
    fun getSpontanUsers(@PathVariable cityId: Int): List<SpontanUserInfo> {
        return spontanUserService.getSpontanUsers(cityId)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @GetMapping("/user/{cityId}/{userId}/credentials")
    fun getSpontanUserCredentials(
        @PathVariable cityId: Int,
        @PathVariable userId: Long
    ): SpontanUserCredentials {
        return spontanUserService.getCredentials(cityId, userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Credentials not found")
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @DeleteMapping("/user/{cityId}/{userId}")
    fun deleteSpontanUser(
        @PathVariable cityId: Int,
        @PathVariable userId: Long
    ) {
        spontanUserService.deleteSpontanUser(cityId, userId)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PutMapping("/group/{assignmentId}/user")
    fun assignUserToGroup(
        @PathVariable assignmentId: Long,
        @RequestBody request: AssignSpontanUserRequest
    ) {
        spontanUserService.assignUserToGroup(assignmentId, request.spontanUserId)
    }

}
