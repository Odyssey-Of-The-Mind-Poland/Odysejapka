package odyseja.odysejapka.rak

import Team
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class HtmlGeneratorService(private val templateEngine: TemplateEngine) {

    fun generateHtmlResults(teams: List<Team>): String {
        val groups: List<FinalScoreGroup> = RakCalculator().calculateScores(teams)
        val context = Context().apply {
            setVariable("groups", groups)
        }

        return templateEngine.process("results.html", context)
    }
}