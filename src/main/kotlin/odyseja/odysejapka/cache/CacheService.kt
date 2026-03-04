package odyseja.odysejapka.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class CacheService(
    private val repository: CacheRepository,
    private val objectMapper: ObjectMapper
) {

    fun <T> get(key: String, type: Class<T>): T? {
        val entity = repository.findById(key).orElse(null) ?: return null
        return objectMapper.readValue(entity.value, type)
    }

    fun <T> get(key: String, typeRef: TypeReference<T>): T? {
        val entity = repository.findById(key).orElse(null) ?: return null
        return objectMapper.readValue(entity.value, typeRef)
    }

    fun put(key: String, value: Any) {
        val json = objectMapper.writeValueAsString(value)
        repository.save(CacheEntity(key = key, value = json))
    }

    fun delete(key: String) {
        repository.findById(key).ifPresent { repository.delete(it) }
    }
}
