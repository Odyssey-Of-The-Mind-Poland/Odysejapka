package odyseja.odysejapka.timetable

import odyseja.odysejapka.age.AgeEntity
import odyseja.odysejapka.age.AgeRepository
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityRepository
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
    private val ageRepository: AgeRepository,
    private val cityRepository: CityRepository,
    private val changeService: ChangeService
) {

    fun getAll(): List<Performance> {
        return timeTableRepository.findAll().sortedBy { it?.performance ?: "0" }.map {
            Performance(
                it!!.id,
                it.cityEntity.name,
                it.team,
                it.problemEntity.id,
                it.ageEntity.id,
                it.stageEntity.number,
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
    }

    fun addPerformance(performances: List<Performance>): List<PerformanceEntity> {
        val cityEntity: CityEntity =
            cityRepository.findFirstByName(performances[0].city) ?: cityRepository.save(
                CityEntity(0, performances[0].city)
            )
        timeTableRepository.deleteByCityEntity(cityEntity)
        val per: List<PerformanceEntity> = performances.map {
            PerformanceEntity(
                it.id,
                getCity(it.city),
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
        timeTableRepository.deleteById(id)

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
        return cityRepository.findFirstByName(city) ?: cityRepository.save(CityEntity(0, city))
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

    fun deleteCity(cityId: Int) {
        timeTableRepository.deleteByCityEntity(cityRepository.findFirstById(cityId))
    }
}