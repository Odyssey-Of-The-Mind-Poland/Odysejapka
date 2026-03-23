package odyseja.odysejapka.timetable

import odyseja.odysejapka.age.AgeEntity
import odyseja.odysejapka.age.AgeRepository
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityService
import odyseja.odysejapka.form.TeamResultRepository
import odyseja.odysejapka.problem.ProblemEntity
import odyseja.odysejapka.problem.ProblemRepository
import odyseja.odysejapka.spontan.SpontanResultRepository
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
    private val ageRepository: AgeRepository,
    private val teamResultRepository: TeamResultRepository,
    private val spontanResultRepository: SpontanResultRepository,
    private val cityService: CityService,
    private val changeService: ChangeService
) {

    fun getFinals(): List<Performance> {
        return timeTableRepository.findAllByCityEntity_Id(0).map { it.toPerformance() }
    }

    fun addPerformances(performances: List<Performance>, cityEntity: CityEntity): List<PerformanceEntity> {
        val performanceIdsToDelete = timeTableRepository.findAllByCityEntity_Id(cityEntity.id).map { it.id }
        deleteResultsForPerformances(performanceIdsToDelete)
        timeTableRepository.deleteByCityEntity(cityEntity)
        val per: List<PerformanceEntity> = performances.map {
            PerformanceEntity(
                it.id,
                cityEntity,
                it.team,
                getProblem(it.problem),
                getAge(it.age),
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
            getCity(performance.city),
            performance.team,
            getProblem(performance.problem),
            getAge(performance.age),
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
        val pToEdit = timeTableRepository.findById(performance.id).get()
        pToEdit.cityEntity = getCity(performance.city)
        pToEdit.team = performance.team
        pToEdit.problemEntity = getProblem(performance.problem)
        pToEdit.ageEntity = getAge(performance.age)
        pToEdit.stageEntity = getStage(performance.stage, performance.city)
        pToEdit.performance = performance.performance
        pToEdit.spontan = performance.spontan
        timeTableRepository.save(pToEdit)
    }

    fun delPerformance(id: Int) {
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


    fun getAge(age: Int): AgeEntity {
        return ageRepository.findFirstById(age) ?: ageRepository.save(
            AgeEntity(
                age,
                age.toString()
            )
        )
    }

    fun getStage(stageNumber: Int, cityName: String): StageEntity {
        val city = getCity(cityName)
        val stage = stageRepository.findFirstByNumberAndCityEntity(
            stageNumber,
            city
        )

        return stage ?: stageRepository.save(StageEntity(0, stageNumber, "Scena nr. $stage", city))
    }

    fun getCity(city: String): CityEntity {
        return cityService.getCityByName(city)
    }

    fun getProblem(problem: Int): ProblemEntity {
        return problemRepository.findFirstById(problem) ?: problemRepository.save(
            ProblemEntity(
                problem,
                problem.toString()
            )
        )
    }

    fun getByCity(cityId: Int): List<Performance> {
        return timeTableRepository.findAllByCityEntity_Id(cityId).map { it.toPerformance() }
    }

    @Transactional
    fun deleteByCity(cityId: Int) {
        val city = cityService.getCity(cityId)
        val performanceIds = timeTableRepository.findAllByCityEntity_Id(cityId).map { it.id }
        deleteResultsForPerformances(performanceIds)
        timeTableRepository.deleteByCityEntity(city)
    }

    fun getPerformance(performanceId: Int): Performance {
        return (timeTableRepository.findById(performanceId)
            .orElseThrow { IllegalArgumentException("Performance $performanceId not found") }
            ?.toPerformance() ?: IllegalArgumentException("Performance $performanceId not found")) as Performance
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