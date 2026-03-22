package odyseja.odysejapka.timetable

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.age.AgeService
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.problem.ProblemEntity
import odyseja.odysejapka.problem.ProblemRepository
import odyseja.odysejapka.stage.StageEntity
import odyseja.odysejapka.stage.StageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TimeTableService(
    private val timeTableRepository: PerformanceRepository,
    private val problemRepository: ProblemRepository,
    private val stageRepository: StageRepository,
    private val ageService: AgeService,
    private val cityService: CityService,
    private val changeService: ChangeService
) {

    fun getFinals(): List<Performance> {
        return timeTableRepository.findAllByCityEntityId(0).map { it.toPerformance() }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun addPerformances(performances: List<Performance>, cityId: Int): List<PerformanceEntity> {
        clearTimetableByCity(cityId)
        val per: List<PerformanceEntity> = performances.map {
            PerformanceEntity(
                it.id,
                cityService.getCity(cityId),
                it.team,
                getProblem(it.problem),
                ageService.getAge(it.age),
                getStage(it.stage, it.city),
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
            getProblem(performance.problem),
            ageService.getAge(performance.age),
            getStage(performance.stage, performance.city),
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

    fun updatePerformance(performance: Performance) {
        val pToEdit = timeTableRepository.findFirstById(performance.id)
            ?: throw EntityNotFoundException("Nie znaleziono przedstawienia o ID ${performance.id}")

        pToEdit.cityEntity = cityService.getCityByName(performance.city)
        pToEdit.team = performance.team
        pToEdit.problemEntity = getProblem(performance.problem)
        pToEdit.ageEntity = ageService.getAge(performance.age)
        pToEdit.stageEntity = getStage(performance.stage, performance.city)
        pToEdit.performance = performance.performance
        pToEdit.spontan = performance.spontan
        timeTableRepository.save(pToEdit)
        changeService.updateVersion()
    }

    fun deletePerformance(id: Int) {
        if (!timeTableRepository.existsById(id))
            throw EntityNotFoundException("Nie znaleziono przedstawienia o ID $id")
        timeTableRepository.deleteById(id)
        changeService.updateVersion()
    }

    @Transactional
    fun clearTimetable() {
        timeTableRepository.deleteAll()
        changeService.updateVersion()
    }

    @Transactional
    fun clearTimetableByCity(cityId: Int) {
        val city = cityService.getCity(cityId)
        timeTableRepository.deleteByCityEntity(city)
        changeService.updateVersion()
    }


    fun getStage(stageNumber: Int, cityName: String): StageEntity {
        val city = cityService.getCityByName(cityName)
        val stage = stageRepository.findFirstByNumberAndCityEntity(
            stageNumber,
            city
        )

        return stage ?: stageRepository.save(StageEntity(0, stageNumber, "Scena nr. $stage", city))
    }

    fun getProblem(problem: Int): ProblemEntity {
        return problemRepository.findFirstById(problem) ?: problemRepository.save(
            ProblemEntity(
                problem,
                problem.toString()
            )
        )
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
}