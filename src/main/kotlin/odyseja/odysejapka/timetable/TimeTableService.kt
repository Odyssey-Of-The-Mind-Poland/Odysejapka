package odyseja.odysejapka.timetable

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.problem.ProblemService
import odyseja.odysejapka.stage.StageService
import odyseja.odysejapka.form.TeamResultRepository
import odyseja.odysejapka.spontan.SpontanResultRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TimeTableService(
    private val timeTableRepository: PerformanceRepository,
    private val problemService: ProblemService,
    private val ageService: AgeService,
    private val cityService: CityService,
    private val changeService: ChangeService,
    private val stageService: StageService,
    private val teamResultRepository: TeamResultRepository,
    private val spontanResultRepository: SpontanResultRepository
    ) {

    fun getFinals(): List<Performance> {
        val finals = cityService.getFinals()
        return getPerformanceEntitiesByCity(finals.id).map { it.toPerformance() }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun addPerformances(performances: List<Performance>, cityId: Int): List<PerformanceEntity> {
        val performanceIdsToDelete = getPerformancesByCity(cityId).map { it.id }
        deleteResultsForPerformances(performanceIdsToDelete)
        clearTimetableByCity(cityId)
        val per: List<PerformanceEntity> = performances.map {
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
        timeTableRepository.saveAll(per)

        changeService.updateVersion()
        return per
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun addPerformance(performance: Performance): PerformanceEntity {
        val per = PerformanceEntity(
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
        timeTableRepository.save(per)

        changeService.updateVersion()
        return per
    }

    @Transactional
    fun updatePerformance(performance: Performance) {
        val pToEdit = timeTableRepository.findFirstById(performance.id)
            ?: throw EntityNotFoundException("Nie znaleziono przedstawienia o ID ${performance.id}")

        pToEdit.cityEntity = cityService.getCityByName(performance.city)
        pToEdit.team = performance.team
        pToEdit.problemEntity = problemService.getProblem(performance.problem)
        pToEdit.ageEntity = ageService.getAge(performance.age)
        pToEdit.stageEntity = stageService.getOrCreateStageByNumber(performance.city, performance.stage)
        pToEdit.performance = performance.performance
        pToEdit.spontan = performance.spontan
        timeTableRepository.save(pToEdit)
        changeService.updateVersion()
    }

    @Transactional
    fun deletePerformance(id: Int) {
        if (!timeTableRepository.existsById(id))
            throw EntityNotFoundException("Nie znaleziono przedstawienia o ID $id")
        deleteResultsForPerformance(id)
        timeTableRepository.deleteById(id)
        changeService.updateVersion()
    }

    @Transactional
    fun clearTimetable() {
        val performanceIds = timeTableRepository.findAll().mapNotNull { it?.id }
        deleteResultsForPerformances(performanceIds)
        timeTableRepository.deleteAll()
        changeService.updateVersion()
    }

    @Transactional
    fun clearTimetableByCity(cityId: Int) {
        val city = cityService.getCity(cityId)
        val performanceIds = timeTableRepository.findAllByCityEntity_Id(cityId).map { it.id }
        deleteResultsForPerformances(performanceIds)
        timeTableRepository.deleteByCityEntity(city)
        changeService.updateVersion()
    }

    fun getPerformanceEntitiesByCity(cityId: Int): List<PerformanceEntity> {
        return timeTableRepository.findAllByCityEntityId(cityId)
    }

    fun getAllPerformanceEntities(): Iterable<PerformanceEntity?> {
        return timeTableRepository.findAll()
    }

    fun getPerformancesByCity(cityId: Int): List<Performance> {
        return getPerformanceEntitiesByCity(cityId).map { it.toPerformance() }
    }

    fun getPerformanceEntity(performanceId: Int): PerformanceEntity {
        val performance = timeTableRepository.findFirstById(performanceId)
            ?: throw EntityNotFoundException("Nie znaleziono przedstawienia o ID $performanceId")
        return performance
    }

    fun getPerformance(performanceId: Int): Performance {
        return getPerformanceEntity(performanceId).toPerformance()
    }

    private fun deleteResultsForPerformance(performanceId: Int) {
        teamResultRepository.deleteByPerformanceId(performanceId)
        spontanResultRepository.deleteByPerformanceId(performanceId)
    }

    private fun deleteResultsForPerformances(performanceIds: List<Int>) {
        if (performanceIds.isEmpty()) {
            return
        }
        teamResultRepository.deleteAllByPerformanceIdIn(performanceIds)
        spontanResultRepository.deleteAllByPerformanceIdIn(performanceIds)
    }
}