package odyseja.odysejapka.stage

import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StageService(
    private val stageRepository: StageRepository,
    private val cityRepository: CityRepository,
    private val changeService: ChangeService
) {

  fun getStagesByCity(city: Int): List<Stage> {
    return stageRepository.findAllByCityEntity(cityRepository.findById(city)).map {
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
      val toEdit: StageEntity = stageRepository.findFirstByNumberAndCityEntity(stage.number, cityRepository.findFirstById(stage.city))
        ?: stageRepository.save(StageEntity(0, stage.number, stage.name, cityRepository.findFirstById(stage.city)))
      toEdit.name = stage.name
      stageRepository.save(toEdit)
    }

    changeService.updateVersion()
  }

  @Transactional
  fun clearStagesByCity(cityId: Int) {
    stageRepository.deleteByCityEntity(cityRepository.findFirstById(cityId))
    changeService.updateVersion()
  }
}