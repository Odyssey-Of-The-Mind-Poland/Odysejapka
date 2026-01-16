package odyseja.odysejapka.form

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class TeamFormHtmlGeneratorService(
    private val templateEngine: TemplateEngine,
    private val teamFormService: TeamFormService
) {

    fun generateHtml(performanceId: Int): String {
        val teamForm = teamFormService.getTeamForm(performanceId)
        val rawTeamForm = TeamFormToRawTeamFormConverter.convert(teamForm)
        
        val context = Context().apply {
            setVariable("data", rawTeamForm)
        }

        return templateEngine.process("team-form-preview.html", context)
    }
}

