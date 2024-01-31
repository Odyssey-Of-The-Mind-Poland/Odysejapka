package odyseja.odysejapka.gad

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class GadCommandService(
    private val repository: GadCommandRepository,
    private val objectMapper: ObjectMapper
) {

    fun saveCommand(command: GenerateGadCommand) {
        val json =
            repository.save(GadCommandEntity(jsonData = objectMapper.writeValueAsString(command)))
    }

    fun getCommand(): GenerateGadCommand {
        val entity = repository.findFirstByOrderByIdDesc()
        if (entity == null) {
            val command = GenerateGadCommand(
                templatesFolderId = "",
                destinationFolderId = "",
                zspId = "",
                problemPunctuationCells = mapOf()
            )
            return command
        }
        return objectMapper.readValue(entity.jsonData, GenerateGadCommand::class.java)
    }

}