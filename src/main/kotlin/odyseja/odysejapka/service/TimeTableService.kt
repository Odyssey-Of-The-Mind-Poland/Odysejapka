package odyseja.odysejapka.service

import odyseja.odysejapka.domain.*
import odyseja.odysejapka.port.TimeTableUseCase
import org.springframework.stereotype.Service

@Service
internal class TimeTableService(
  private val timeTableRepository: PerformanceRepository,
  private val problemRepository: ProblemRepository,
  private val stageRepository: StageRepository,
  private val ageRepository: AgeRepository,
  private val cityRepository: CityRepository
) : TimeTableUseCase {

  override fun getAll(): List<Performance> {
    return timeTableRepository.findAll().map {
      Performance(
        it!!.id,
        it.cityEntity.name,
        it.team,
        it.problemEntity.id,
        it.ageEntity.id,
        it.stageEntity.number,
        it.performance,
        it.spontan
      )
    }
  }

  override fun addPerformance(performances: List<Performance>): List<PerformanceEntity> {
    val cityEntity: CityEntity =
      cityRepository.findFirstByName(performances[0].city) ?: cityRepository.save(
        CityEntity(0, performances[0].city)
      )
    timeTableRepository.deleteByCityEntity(cityEntity)
    val per: List<PerformanceEntity> = performances.map {
      PerformanceEntity(
        it.id,
        cityRepository.findFirstByName(it.city) ?: cityRepository.save(CityEntity(0, it.city)),
        it.team,
        problemRepository.findFirstById(it.problem),
        ageRepository.findFirstById(it.age),
        stageRepository.findFirstByNumberAndCityEntity(it.stage, cityRepository.findFirstByName(it.city))
          ?: stageRepository.save(StageEntity(0, it.stage, "Scena nr. " + it.stage, cityEntity)),
        it.performance,
        it.spontan
      )
    }
    timeTableRepository.saveAll(per)
    return per
  }

  override fun updatePerformance(performance: Performance) {
    val pToEdit = timeTableRepository.findById(performance.id).get()
    pToEdit.cityEntity = cityRepository.findFirstByName(performance.city)
      ?: cityRepository.save(CityEntity(0, performance.city))
    pToEdit.team = performance.team
    pToEdit.problemEntity = problemRepository.findFirstById(performance.problem)
    pToEdit.ageEntity = ageRepository.findFirstById(performance.age)
    pToEdit.stageEntity = stageRepository.findFirstById(performance.stage)
      ?: stageRepository.save(
        StageEntity(
          0,
          performance.stage,
          "Scena nr. " + performance.stage,
          pToEdit.cityEntity
        )
      )
    pToEdit.performance = performance.performance
    pToEdit.spontan = performance.spontan
    timeTableRepository.save(pToEdit)
  }

  override fun delPerformance(id: Int) {
    timeTableRepository.deleteById(id)
  }
}