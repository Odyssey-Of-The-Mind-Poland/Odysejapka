package odyseja.odysejapka.form

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class PerformanceResultsConverter : AttributeConverter<PerformanceResultsRequest, String> {

    companion object {
        private val objectMapper: ObjectMapper = jacksonObjectMapper()
    }

    override fun convertToDatabaseColumn(attribute: PerformanceResultsRequest?): String? {
        if (attribute == null) return null
        return try {
            objectMapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            null
        }
    }

    override fun convertToEntityAttribute(dbData: String?): PerformanceResultsRequest? {
        if (dbData == null || dbData.isBlank()) return null
        return try {
            objectMapper.readValue(dbData, PerformanceResultsRequest::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

