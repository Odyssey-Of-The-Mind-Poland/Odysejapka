package odyseja.odysejapka.spontan

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/spontan")
class SpontanController(
    private val spontanService: SpontanService,
    private val spontanResultService: SpontanResultService
) {

    @GetMapping
    fun getAll(): List<SpontanDefinition> {
        return spontanService.getAll()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): SpontanDefinition {
        return spontanService.getById(id)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PostMapping
    fun create(@RequestBody definition: SpontanDefinition): SpontanDefinition {
        return spontanService.create(definition)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody definition: SpontanDefinition): SpontanDefinition {
        return spontanService.update(id, definition)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        spontanService.delete(id)
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
        return spontanResultService.getGroupTeams(cityId, groupId)
    }

    @PutMapping("/result/{performanceId}")
    fun setResults(
        @PathVariable performanceId: Int,
        @RequestBody request: SpontanResultsRequest
    ): SpontanTeamResult {
        return spontanResultService.setResults(performanceId, request)
    }
}
