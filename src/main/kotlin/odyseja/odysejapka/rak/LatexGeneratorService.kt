package odyseja.odysejapka.rak

import odyseja.odysejapka.gad.Team
import com.samskivert.mustache.Mustache
import odyseja.odysejapka.problem.ProblemEntity
import odyseja.odysejapka.problem.ProblemService
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class LatexGeneratorService(
    private val problemService: ProblemService,
    private val texapiClient: TexapiClient
) {

    fun generatePdf(teams: List<Team>, isRegion: Boolean = false, contestName: String? = null): ByteArray {
        val latex = generateLatex(teams, isRegion, contestName)
        val fontFiles = loadFontFiles()
        require(fontFiles.any { it.first == "Ubuntu-Regular.ttf" }) {
            "Ubuntu fonts required for LaTeX PDF. Add static/fonts/Ubuntu-Regular.ttf (and Bold/Italic/BoldItalic) to resources."
        }
        return texapiClient.compileFilesToPdf(latex, fontFiles)
    }

    private fun loadFontFiles(): List<Pair<String, ByteArray>> {
        val fontNames = listOf("Ubuntu-Regular.ttf", "Ubuntu-Bold.ttf", "Ubuntu-Italic.ttf", "Ubuntu-BoldItalic.ttf")
        return fontNames.mapNotNull { name ->
            val resource = ClassPathResource("static/fonts/$name")
            if (!resource.exists()) return@mapNotNull null
            resource.inputStream.use { it.readBytes() }.takeIf { it.isNotEmpty() }?.let { name to it }
        }
    }

    fun generateLatex(teams: List<Team>, isRegion: Boolean = false, contestName: String? = null): String {
        val initialGroups = RakCalculator().calculateScores(teams, isRegion)
            .sortedWith(compareBy({ it.problem }, { it.division }))
        val splitGroups = initialGroups.flatMap { splitLargeGroup(it) }
        val problems = problemService.getProblems().filterNotNull()
        val context = buildContext(splitGroups, problems, contestName)
        val templateSource = ClassPathResource("templates/latex/results.tex").inputStream.reader().readText()
        return Mustache.compiler()
            .withDelims("<% %>")
            .escapeHTML(false)
            .compile(templateSource)
            .execute(context)
    }

    private fun splitLargeGroup(group: FinalScoreGroup): List<FinalScoreGroup> {
        if (group.teamScores.size <= 20) return listOf(group)
        return group.teamScores.chunked(20).map { teamScores ->
            FinalScoreGroup(
                problem = group.problem,
                division = group.division,
                league = group.league,
                teamScores = teamScores
            )
        }
    }

    private fun buildContext(
        groups: List<FinalScoreGroup>,
        problems: List<ProblemEntity>,
        contestName: String?
    ): LatexResultsContext {
        val escapedContestName = escapeLatex(
            contestName?.takeIf { it.isNotBlank() } ?: "34. Ogólnopolski Finał Odysei Umysłu"
        )
        val latexGroups = groups.map { group ->
            val problemName = problems.find { it.id == group.problem }?.name?.takeIf { it.isNotBlank() }
                ?: "Problem ${group.problem}"
            LatexGroup(
                problem = group.problem,
                division = group.division,
                problemName = escapeLatex(problemName),
                showWeight = group.problem == 4,
                teamScores = group.teamScores.map { score -> toLatexRow(score, group.problem) }
            )
        }
        return LatexResultsContext(contestName = escapedContestName, groups = latexGroups)
    }

    private fun toLatexRow(score: FinalTeamScore, problem: Int): LatexRow {
        val placeContent = if (score.team.ranatra) "${score.place}\\\\[1pt]{\\color{orange}[RF]}" else "${score.place}"
        val placeStyle = if (score.isWinner) "{\\color{blue}\\bfseries $placeContent}" else placeContent
        val teamStyle = if (score.isWinner) "{\\color{blue}\\bfseries ${escapeLatex(score.team.shortTeamName)}}" else escapeLatex(score.team.shortTeamName)
        val cityStyle = if (score.isWinner) "{\\color{blue}\\bfseries ${escapeLatex(score.team.city)}}" else "{\\color{gray} ${escapeLatex(score.team.city)}}"
        val penaltyStyle = if ((score.team.penaltyScore ?: 0f) != 0f) "\\textcolor{red}{${formatInt(score.team.penaltyScore!!)}}" else "{\\color{gray} ${formatInt(score.team.penaltyScore ?: 0f)}}"
        val showWeight = problem == 4
        val (weightHeld, balsaScore) = if (showWeight) {
            formatNum(score.team.weightHeld ?: 0f) to formatNum(score.balsaScore)
        } else {
            "" to ""
        }
        return LatexRow(
            placeStyle = placeStyle,
            teamStyle = teamStyle,
            cityStyle = cityStyle,
            showWeight = showWeight,
            weightHeld = weightHeld,
            balsaScore = balsaScore,
            longTermScore = formatNum(score.longTermScore),
            styleScore = formatNum(score.styleScore),
            spontaneousScore = formatNum(score.spontaneousScore),
            total = formatNum(score.total),
            rawLongTerm = formatNum(score.team.longTermScore ?: 0f),
            rawStyle = formatNum(score.team.styleScore ?: 0f),
            rawSpontaneous = formatNum(score.team.spontaneousScore ?: 0f),
            penaltyStyle = penaltyStyle
        )
    }

    private fun formatNum(value: Float): String = "%.2f".format(Locale.ENGLISH, value)
    private fun formatNum(value: Double): String = "%.2f".format(Locale.ENGLISH, value)
    private fun formatInt(value: Float): String = "%.0f".format(Locale.ENGLISH, value)

    private fun escapeLatex(s: String): String {
        return s
            .replace("\\", "\\\\")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("&", "\\&")
            .replace("#", "\\#")
            .replace("$", "\\$")
            .replace("%", "\\%")
            .replace("_", "\\_")
            .replace("~", "\\textasciitilde{}")
    }
}
