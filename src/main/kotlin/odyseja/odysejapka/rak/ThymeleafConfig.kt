package odyseja.odysejapka.rak

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver

@Configuration
class ThymeleafConfig {

    @Value("\${spring.thymeleaf.prefix}")
    private lateinit var templatePrefix: String

//    @Bean
//    fun templateResolver(): FileTemplateResolver {
//        val templateResolver = FileTemplateResolver()
//        templateResolver.prefix = templatePrefix
//        templateResolver.suffix = ".html"
//        templateResolver.templateMode = TemplateMode.HTML
//        templateResolver.characterEncoding = "UTF-8"
//        templateResolver.isCacheable = false
//        return templateResolver
//    }

    @Bean
    fun templateEngine(databaseTemplateResolver: DatabaseTemplateResolver): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(databaseTemplateResolver)
        templateEngine.enableSpringELCompiler = true
        return templateEngine
    }

}