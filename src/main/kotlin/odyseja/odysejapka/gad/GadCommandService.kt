package odyseja.odysejapka.gad

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class GadCommandService(
    private val repository: GadCommandRepository,
    private val objectMapper: ObjectMapper
) {

    fun saveCommand(command: GenerateGadCommand, cityId: Int?) {
        repository.save(
            GadCommandEntity(
                cityId = cityId,
                jsonData = objectMapper.writeValueAsString(command)
            )
        )
    }

    fun getCommand(cityId: Int?): GenerateGadCommand {
        val entity = if (cityId != null) {
            repository.findFirstByCityIdOrderByIdDesc(cityId)
        } else {
            repository.findFirstByOrderByIdDesc()
        }

        return if (entity == null) {
            GenerateGadCommand(
                templatesFolderId = "",
                destinationFolderId = "",
                zspId = "",
                problemPunctuationCells = mapOf()
            )
        } else {
            objectMapper.readValue(entity.jsonData, GenerateGadCommand::class.java)
        }
    }

}