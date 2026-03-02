package odyseja.odysejapka.spontan

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class SpontanResultsConverter : AttributeConverter<List<SpontanResultEntry>, String> {

    companion object {
        private val objectMapper: ObjectMapper = jacksonObjectMapper()
    }

    override fun convertToDatabaseColumn(attribute: List<SpontanResultEntry>?): String? {
        if (attribute == null) return null
        return try {
            objectMapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            null
        }
    }

    override fun convertToEntityAttribute(dbData: String?): List<SpontanResultEntry>? {
        if (dbData == null || dbData.isBlank()) return null
        return try {
            objectMapper.readValue(dbData)
        } catch (e: Exception) {
            null
        }
    }
}
