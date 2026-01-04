package odyseja.odysejapka.rak

import Team
import odyseja.odysejapka.problem.ProblemService
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine

@Service
class HtmlGeneratorService(private val templateEngine: TemplateEngine, private val problemService: ProblemService) {

    fun generateShortResults(teams: List<Team>): String {
        val context = ResultsContextBuilder(teams, problemService.getProblems()).short()
        return templateEngine.process("short-results.html", context)
    }

    fun generateHtmlResults(teams: List<Team>): String {
        val context = ResultsContextBuilder(teams, problemService.getProblems()).full()
        return templateEngine.process("results.html", context)
    }
}