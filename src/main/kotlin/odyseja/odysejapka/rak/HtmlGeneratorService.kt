package odyseja.odysejapka.rak

import Team
import odyseja.odysejapka.problem.ProblemService
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class HtmlGeneratorService(private val templateEngine: TemplateEngine, private val problemService: ProblemService) {

    fun generateHtmlResults(teams: List<Team>): String {
        val initialGroups: List<FinalScoreGroup> =
            RakCalculator().calculateScores(teams).sortedWith(compareBy({ it.problem }, { it.division }))
        val splitGroups = initialGroups.flatMap { splitLargeGroup(it) }
        val context = Context().apply {
            setVariable("groups", splitGroups)
            setVariable("problems", problemService.getProblems())
        }

        return templateEngine.process("results.html", context)
    }

    private fun splitLargeGroup(group: FinalScoreGroup): List<FinalScoreGroup> {
        if (group.teamScores.size <= 20) {
            return listOf(group)
        }

        val chunks = group.teamScores.chunked(20)
        return chunks.map { teamScores ->
            FinalScoreGroup(
                problem = group.problem,
                division = group.division,
                league = group.league,
                teamScores = teamScores
            )
        }
    }
}