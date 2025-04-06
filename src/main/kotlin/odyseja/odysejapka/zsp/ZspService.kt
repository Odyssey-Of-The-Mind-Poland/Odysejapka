package odyseja.odysejapka.zsp

import Team
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ZspService {
    @Value("\${zsp.sheet.id}")
    private lateinit var zspId: String
    
    private var teams: List<Team> = emptyList()
    private var lastFetchTime: LocalDateTime? = null
    private var groupedTeams: Map<Int, List<TeamGroup>> = emptyMap()
    
    fun refreshTeams() {
        try {
            val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
            teams = sheetsAdapter.getAllTeams()
            lastFetchTime = LocalDateTime.now()
            groupTeams()
        } catch (e: Exception) {
            println("Error fetching teams: ${e.message}")
        }
    }
    
    private fun groupTeams() {
        groupedTeams = teams
            .groupBy { it.stage } // First level: stage
            .mapValues { (_, teamsInStage) ->
                teamsInStage
                    .groupBy { TeamGroupKey(it.getProblem().toInt(), it.getAge().toInt(), it.league) }
                    .map { (key, teamsInGroup) ->
                        val scoredTeamsCount = teamsInGroup.count { it.longTermScore != null && it.longTermScore != 0f }
                        TeamGroup(
                            problem = key.problem,
                            age = key.age,
                            league = key.league,
                            teams = teamsInGroup,
                            allTeamsCount = teamsInGroup.size,
                            scoredTeamsCount = scoredTeamsCount
                        )
                    }
            }
    }
    
    fun getTeams(): List<Team> {
        return teams
    }
    
    fun getGroupedTeams(): Map<Int, List<TeamGroup>> {
        return groupedTeams
    }
    
    fun getLastFetchTime(): LocalDateTime? {
        return lastFetchTime
    }
    
    data class TeamGroupKey(val problem: Int, val age: Int, val league: String)
    
    data class TeamGroup(
        val problem: Int,
        val age: Int,
        val league: String,
        val teams: List<Team>,
        val allTeamsCount: Int,
        val scoredTeamsCount: Int
    )
} 