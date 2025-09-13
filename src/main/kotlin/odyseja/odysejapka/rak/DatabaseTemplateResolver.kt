package odyseja.odysejapka.rak

import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.thymeleaf.IEngineConfiguration
import org.thymeleaf.cache.NonCacheableCacheEntryValidity
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresource.ITemplateResource
import org.thymeleaf.templateresource.StringTemplateResource
import org.thymeleaf.templateresolver.ITemplateResolver
import org.thymeleaf.templateresolver.TemplateResolution

@Component
class DatabaseTemplateResolver(
    private val templateStore: TemplateStoreService
) : ITemplateResolver, Ordered {

    var resolverName: String = "dbTemplateResolver"
    var orderValue: Int = 1

    override fun getName(): String = resolverName
    override fun getOrder(): Int = orderValue

    override fun resolveTemplate(
        configuration: IEngineConfiguration,
        ownerTemplate: String?,
        template: String,
        templateResolutionAttributes: MutableMap<String, Any>?
    ): TemplateResolution? {
        val defaultBody = defaultFor(template)

        val entity = templateStore.getOrCreate(template, defaultBody)

        val resource: ITemplateResource = StringTemplateResource(entity.content)

        return TemplateResolution(resource, TemplateMode.TEXT, NonCacheableCacheEntryValidity() )
    }

    private fun defaultFor(name: String): String =
        """
                \documentclass{article}
                \begin{document}
                % Auto-created DB template: $name
                Hello World!
                \end{document}
            """
}
