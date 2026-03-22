package odyseja.odysejapka.rak

import odyseja.odysejapka.gad.Team
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.form.FormState
import odyseja.odysejapka.form.TeamResultEntity
import odyseja.odysejapka.form.TeamResultRepository
import odyseja.odysejapka.spontan.SpontanResultEntity
import odyseja.odysejapka.spontan.SpontanResultRepository
import odyseja.odysejapka.timetable.PerformanceEntity
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service

data class ResultsStatusResponse(
    val totalPerformances: Int,
    val unapprovedForms: List<String>,
    val missingSpontans: List<String>
)

@Service
class CityResultsService(
    private val performanceRepository: PerformanceRepository,
    private val teamResultRepository: TeamResultRepository,
    private val spontanResultRepository: SpontanResultRepository,
    private val cityService: CityService,
    private val latexGeneratorService: LatexGeneratorService
) {

    fun getResultsStatus(cityId: Int): ResultsStatusResponse {
        val performances = performanceRepository.findAllByCityEntity_Id(cityId)
            .filter { !it.isExcludedFromScoring() }
        val performanceIds = performances.map { it.id }

        val teamResults = teamResultRepository.findAllByPerformanceIdIn(performanceIds)
            .associateBy { it.performanceId }
        val spontanResults = spontanResultRepository.findAllByPerformanceIdIn(performanceIds)
            .associateBy { it.performanceId }

        val unapprovedForms = performances.filter { perf ->
            val tr = teamResults[perf.id]
            tr == null || tr.formState != FormState.APPROVED
        }.map { it.team }

        val missingSpontans = performances.filter { perf ->
            val sr = spontanResults[perf.id]
            sr == null || sr.rawSpontan == null
        }.map { it.team }

        return ResultsStatusResponse(
            totalPerformances = performances.size,
            unapprovedForms = unapprovedForms,
            missingSpontans = missingSpontans
        )
    }

    fun generateResultsPdf(cityId: Int): ByteArray {
        val city = cityService.getCity(cityId)
        val isRegion = !city.name.equals("finał", ignoreCase = true)
        val teams = buildTeamsFromDb(cityId, city.name)
        return latexGeneratorService.generatePdf(teams, isRegion, city.name)
    }

    private fun buildTeamsFromDb(cityId: Int, cityName: String): List<Team> {
        val performances = performanceRepository.findAllByCityEntity_Id(cityId)
            .filter { !it.isExcludedFromScoring() }
        val performanceIds = performances.map { it.id }

        val teamResults = teamResultRepository.findAllByPerformanceIdIn(performanceIds)
            .associateBy { it.performanceId }
        val spontanResults = spontanResultRepository.findAllByPerformanceIdIn(performanceIds)
            .associateBy { it.performanceId }

        return performances.map { perf ->
            val tr = teamResults[perf.id]
            val sr = spontanResults[perf.id]
            mapToTeam(perf, tr, sr, cityName)
        }
    }

    private fun mapToTeam(
        perf: PerformanceEntity,
        tr: TeamResultEntity?,
        sr: SpontanResultEntity?,
        cityName: String
    ): Team {
        val problem = perf.problemEntity.id
        val age = perf.ageEntity.id
        return Team(
            performanceHour = perf.performance,
            spontanHour = perf.spontan,
            code = "P${problem}G${age}",
            membershipNumber = "DB",
            league = perf.league ?: "",
            part = (perf.part ?: 0).toString(),
            teamName = perf.team,
            shortTeamName = perf.team,
            city = cityName,
            zspRow = perf.zspRow ?: 0,
            day = perf.performanceDay,
            stage = perf.stageEntity.number,
            zspSheet = perf.zspSheet,
            longTermScore = tr?.rawDt?.toFloat(),
            styleScore = tr?.rawStyle?.toFloat(),
            penaltyScore = tr?.rawPenalty?.toFloat(),
            weightHeld = tr?.rawWeight?.toFloat(),
            spontaneousScore = sr?.rawSpontan?.toFloat(),
            ranatra = tr?.ranatra ?: false
        )
    }
}
