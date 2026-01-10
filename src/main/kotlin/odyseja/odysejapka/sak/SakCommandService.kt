package odyseja.odysejapka.sak

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class SakCommandService(
    private val repository: SakCommandRepository,
    private val objectMapper: ObjectMapper
) {

    fun saveCommand(command: GenerateSakCommand, cityId: Int?) {
        repository.save(
            SakCommandEntity(
                cityId = cityId,
                jsonData = objectMapper.writeValueAsString(command)
            )
        )
    }

    fun getCommand(cityId: Int?): GenerateSakCommand {
        val entity = if (cityId != null) {
            repository.findFirstByCityIdOrderByIdDesc(cityId)
        } else {
            repository.findFirstByOrderByIdDesc()
        }

        return if (entity == null) {
            GenerateSakCommand(
                templatesFolderId = "",
                zspId = ""
            )
        } else {
            objectMapper.readValue(entity.jsonData, GenerateSakCommand::class.java)
        }
    }
}


