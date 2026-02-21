package odyseja.odysejapka.rak

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class TexapiConfig {

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}
