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
    private val stageUserService: StageUserService
) {

    @Transactional
    fun getOrCreateStageByNumber(cityId: Int, stageNumber: Int): StageEntity {
        return stageRepository.findFirstByNumberAndCityEntity(stageNumber, cityService.getCity(cityId))
            ?: stageRepository.save(StageEntity(
                0, stageNumber, "Scena $stageNumber", cityService.getCity(cityId)
            ))
    }

    fun getStagesByCity(cityId: Int): List<Stage> {
        return stageRepository.findByCityEntity(cityService.getCity(cityId)).map {
            Stage(
                it!!.id,
                it.number,
                it.cityEntity.id,
                it.name
            )
        }
    }

    @Transactional
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
    fun deleteStagesByCity(cityId: Int) {
        val city = cityService.getCity(cityId)
        stageUserService.deleteStageUsersByCity(cityId)
        stageRepository.deleteByCityEntity(city)
        changeService.updateVersion()
    }
}