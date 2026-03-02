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
        @RequestParam(required = false) league: String?,
        @RequestBody request: AssignSpontanRequest
    ): SpontanGroupAssignment {
        return spontanService.assignSpontan(cityId, problem, age, league, request.spontanDefinitionId)
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @PutMapping("/group/{cityId}/judges")
    fun setJudgeCount(
        @PathVariable cityId: Int,
        @RequestParam problem: Int,
        @RequestParam age: Int,
        @RequestParam(required = false) league: String?,
        @RequestBody request: SetJudgeCountRequest
    ): SpontanGroupAssignment {
        return spontanService.setJudgeCount(cityId, problem, age, league, request.judgeCount)
    }

    @GetMapping("/result/{cityId}/teams")
    fun getGroupTeams(
        @PathVariable cityId: Int,
        @RequestParam problem: Int,
        @RequestParam age: Int,
        @RequestParam(required = false) league: String?
    ): SpontanGroupTeams {
        return spontanResultService.getGroupTeams(cityId, problem, age, league)
    }

    @PutMapping("/result/{performanceId}")
    fun setResults(
        @PathVariable performanceId: Int,
        @RequestBody request: SpontanResultsRequest
    ): SpontanTeamResult {
        return spontanResultService.setResults(performanceId, request)
    }
}
