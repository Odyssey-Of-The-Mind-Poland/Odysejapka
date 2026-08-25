package odyseja.odysejapka.timetable

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.problem.ProblemService
import odyseja.odysejapka.stage.StageService
import odyseja.odysejapka.form.TeamResultService
import odyseja.odysejapka.spontan.SpontanResultService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TimeTableService(
    private val performanceService: PerformanceService,
    private val problemService: ProblemService,
    private val ageService: AgeService,
    private val cityService: CityService,
    private val changeService: ChangeService,
    private val stageService: StageService,
    private val teamResultService: TeamResultService,
    private val spontanResultService: SpontanResultService
    ) {

    fun getFinals(): List<Performance> {
        val finals = cityService.getFinals()
        return performanceService.getPerformanceEntitiesByCity(finals.id)
            .map { it.toPerformance() }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun addPerformances(performances: List<Performance>, cityId: Int): List<PerformanceEntity> {
        val performanceIdsToDelete = performanceService.getPerformancesByCity(cityId).map { it.id }
        deleteResultsForPerformances(performanceIdsToDelete)
        clearTimetableByCity(cityId)
        val performanceEntities: List<PerformanceEntity> = performances.map {
            PerformanceEntity(
                it.id,
                cityService.getCity(cityId),
                it.team,
                problemService.getProblem(it.problem),
                ageService.getAge(it.age),
                stageService.getOrCreateStageByNumber(it.city, it.stage),
                it.performance,
                it.spontan,
                it.part,
                it.performanceDay,
                it.spontanDay,
                it.league,
                it.zspRow,
                it.zspSheet
            )
        }
        performanceService.savePerformances(performanceEntities)

        changeService.updateVersion()
        return performanceEntities
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun addPerformance(performance: Performance): PerformanceEntity {
        val performanceEntity = PerformanceEntity(
            performance.id,
            cityService.getCityByName(performance.city),
            performance.team,
            problemService.getProblem(performance.problem),
            ageService.getAge(performance.age),
            stageService.getOrCreateStageByNumber(performance.city, performance.stage),
            performance.performance,
            performance.spontan,
            performance.part,
            performance.performanceDay,
            performance.spontanDay,
            performance.league,
            performance.zspRow,
            performance.zspSheet
        )
        performanceService.savePerformance(performanceEntity)

        changeService.updateVersion()
        return performanceEntity
    }

    @Transactional
    fun updatePerformance(performance: Performance) {
        val performanceToEdit = performanceService.getPerformanceEntity(performance.id)

        performanceToEdit.cityEntity = cityService.getCityByName(performance.city)
        performanceToEdit.team = performance.team
        performanceToEdit.problemEntity = problemService.getProblem(performance.problem)
        performanceToEdit.ageEntity = ageService.getAge(performance.age)
        performanceToEdit.stageEntity = stageService.getOrCreateStageByNumber(performance.city, performance.stage)
        performanceToEdit.performance = performance.performance
        performanceToEdit.spontan = performance.spontan

        performanceService.savePerformance(performanceToEdit)
        changeService.updateVersion()
    }

    @Transactional
    fun deletePerformance(id: Int) {
        deleteResultsForPerformance(id)
        performanceService.deletePerformance(id)
        changeService.updateVersion()
    }

    @Transactional
    fun clearTimetable() {
        val performanceIds = performanceService.getAllPerformanceEntities()
            .mapNotNull { it?.id }
        deleteResultsForPerformances(performanceIds)
        performanceService.deleteAllPerformances()
        changeService.updateVersion()
    }

    @Transactional
    fun clearTimetableByCity(cityId: Int) {
        val city = cityService.getCity(cityId)
        val performanceIds = performanceService.getPerformanceEntitiesByCity(cityId).map { it.id }
        deleteResultsForPerformances(performanceIds)
        performanceService.deletePerformancesByCity(city)
        changeService.updateVersion()
    }

    private fun deleteResultsForPerformance(performanceId: Int) {
        teamResultService.deleteTeamResult(performanceId)
        spontanResultService.deleteSpontanResult(performanceId)
    }

    private fun deleteResultsForPerformances(performanceIds: List<Int>) {
        if (performanceIds.isEmpty()) {
            return
        }
        teamResultService.deleteTeamResults(performanceIds)
        spontanResultService.deleteSpontanResults(performanceIds)
    }
}