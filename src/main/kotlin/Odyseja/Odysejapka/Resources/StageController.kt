package Odyseja.Odysejapka.Resources

import Odyseja.Odysejapka.data.model.Stage
import Odyseja.Odysejapka.repository.CityRepository
import Odyseja.Odysejapka.repository.StageRepository
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/stage")
class StageController(
        private val stageRepository: StageRepository,
        private val cityRepository: CityRepository
) {

    @GetMapping("{city}")
    fun getStages(@PathVariable city: Int): List<StageDTO> {
        return stageRepository.findAllByCity(cityRepository.findById(city)).map {
            StageDTO(
                    it!!.id,
                    it.number,
                    it.city.id,
                    it.name
            )
        }
    }

    @GetMapping
    fun getStages(): List<StageDTO> {
        return stageRepository.findAll().map {
            StageDTO(
                    it!!.id,
                    it.number,
                    it.city.id,
                    it.name
            )
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping
    fun updateStage(@RequestBody stages: List<StageDTO>) {
        for (stage in stages) {
            val toEdit: Stage = stageRepository.findFirstByNumberAndCity(stage.number, cityRepository.findFirstById(stage.city))
                    ?: stageRepository.save(Stage(0, stage.number, stage.name, cityRepository.findFirstById(stage.city)))
            toEdit.name = stage.name
            stageRepository.save(toEdit)
        }
    }
}

class StageDTO(val id: Int, val number: Int, val city: Int, val name: String)