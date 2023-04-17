package odyseja.odysejapka.service

import odyseja.odysejapka.domain.*
import org.springframework.stereotype.Service

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
        it.league
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
        it.league
      )
    }
    timeTableRepository.saveAll(per)

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

  fun getStage(stage: Int, city: String): StageEntity {
    return stageRepository.findFirstByNumberAndCityEntity(
      stage,
      getCity(city)
    )
      ?: stageRepository.save(StageEntity(0, stage, "Scena nr. $stage", getCity(city)))
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
}