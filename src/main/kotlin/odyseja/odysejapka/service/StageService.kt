package odyseja.odysejapka.service

import odyseja.odysejapka.domain.Stage
import odyseja.odysejapka.domain.StageEntity
import org.springframework.stereotype.Service

@Service
class StageService(
  private val stageRepository: StageRepository,
  private val cityRepository: CityRepository,
  private val changeService: ChangeService
) {

  fun getStages(city: Int): List<Stage> {
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
}