package odyseja.odysejapka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.cfg.CoercionAction
import com.fasterxml.jackson.databind.cfg.CoercionInputShape
import com.fasterxml.jackson.databind.type.LogicalType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfig {

    @Bean
    @Primary
    fun objectMapper(builder: Jackson2ObjectMapperBuilder?): ObjectMapper {
        val objectMapper = if (builder != null) {
            builder.build<ObjectMapper>()
        } else {
            ObjectMapper()
        }
        
        objectMapper.coercionConfigFor(LogicalType.Enum)
            .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull)
        
        return objectMapper
    }
}

