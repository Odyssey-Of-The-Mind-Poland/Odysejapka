package odyseja.odysejapka.rak

import Team
import odyseja.odysejapka.drive.ZspSheetsAdapter
import odyseja.odysejapka.problem.ProblemService
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class HtmlGeneratorService(private val templateEngine: TemplateEngine, private val problemService: ProblemService) {

    fun generateHtmlResults(teams: List<Team>): String {
        val groups: List<FinalScoreGroup> = RakCalculator().calculateScores(teams)
        val context = Context().apply {
            setVariable("groups", groups)
            setVariable("problems", problemService.getProblems())
        }

        return templateEngine.process("results.html", context)
    }
}