package odyseja.odysejapka.zsp

import Team
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/zsp")
class ZspController(private val zspService: ZspService) {

    @GetMapping("/teams")
    fun getTeams(): TeamsResponse {
        return TeamsResponse(
            teams = zspService.getTeams(),
            lastFetchTime = zspService.getLastFetchTime()?.toString() ?: "Never"
        )
    }
    
    @GetMapping("/teams/grouped")
    fun getGroupedTeams(): GroupedTeamsResponse {
        return GroupedTeamsResponse(
            teams = zspService.getGroupedTeams(),
            lastFetchTime = zspService.getLastFetchTime()?.toString() ?: "Never"
        )
    }
    
    data class TeamsResponse(
        val teams: List<Team>,
        val lastFetchTime: String
    )
    
    data class GroupedTeamsResponse(
        val teams: Map<Int, List<ZspService.TeamGroup>>,
        val lastFetchTime: String
    )
} 