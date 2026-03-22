package odyseja.odysejapka.stage

import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StageService(
  private val stageRepository: StageRepository,
  private val cityService: CityService,
  private val changeService: ChangeService,
  private val stageUserRepository: StageUserRepository
) {

  fun getStagesByCity(cityId: Int): List<Stage> {
    return stageRepository.findAllByCityEntity(cityService.getCity(cityId)).map {
      Stage(
        it!!.id,
        it.number,
        it.cityEntity.id,
        it.name
      )
    }
  }

  fun getStages(): List<Stage> {
    return stageRepository.findAll().map {
      Stage(
        it!!.id,
        it.number,
        it.cityEntity.id,
        it.name
      )
    }
  }

  fun updateStage(stages: List<Stage>) {
    for (stage in stages) {
      val toEdit: StageEntity = stageRepository.findFirstByNumberAndCityEntity(stage.number, cityService.getCity(stage.city))
        ?: stageRepository.save(StageEntity(0, stage.number, stage.name, cityService.getCity(stage.city)))
      toEdit.name = stage.name
      stageRepository.save(toEdit)
    }

    changeService.updateVersion()
  }

  @Transactional
  fun clearStagesByCity(cityId: Int) {
    val city = cityService.getCity(cityId)
    stageUserRepository.deleteAllByCityId(cityId)
    stageRepository.deleteByCityEntity(city)
    changeService.updateVersion()
  }
}