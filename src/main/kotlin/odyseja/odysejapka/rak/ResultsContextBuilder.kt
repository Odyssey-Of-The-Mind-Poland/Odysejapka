package odyseja.odysejapka.rak

import Team
import odyseja.odysejapka.problem.ProblemEntity
import org.thymeleaf.context.Context

class ResultsContextBuilder(private val teams: List<Team>, private val problems: List<ProblemEntity?>) {

    fun short(): Context = buildContext(limit = 4)

    fun full(): Context = buildContext()

    private fun buildContext(limit: Int? = null): Context {
        val initialGroups = RakCalculator().calculateScores(teams)
            .sortedWith(compareBy({ it.problem }, { it.division }))

        val groups = if (limit != null) {
            initialGroups.map { it.copy(teamScores = it.teamScores.filter { ts -> ts.place <= limit }) }
                .filter { it.teamScores.isNotEmpty() }
        } else initialGroups

        val splitGroups = groups.flatMap { splitLargeGroup(it) }

        val problemNamesById: Map<Int, String> =
            problems.filterNotNull().associate { it.id to it.name }

        return Context().apply {
            setVariable("groups", splitGroups)
            setVariable("problems", problems)
            setVariable("problemNamesById", problemNamesById)
        }
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