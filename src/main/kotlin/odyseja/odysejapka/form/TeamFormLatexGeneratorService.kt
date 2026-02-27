package odyseja.odysejapka.form

import com.samskivert.mustache.Mustache
import odyseja.odysejapka.rak.TexapiClient
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class TeamFormLatexGeneratorService(
    private val teamFormService: TeamFormService,
    private val texapiClient: TexapiClient
) {

    fun generatePdf(performanceId: Int, english: Boolean = false): ByteArray {
        val teamForm = teamFormService.getTeamForm(performanceId)
        val rawForm = TeamFormToRawTeamFormConverter.convert(teamForm, useTranslation = english)
        val context = rawFormToMustacheContext(rawForm)
        val templateName = if (english) "team-form-en.tex" else "team-form.tex"
        val latex = renderTemplate(context, templateName)
        val fontFiles = loadFontFiles()
        require(fontFiles.any { it.first == "Ubuntu-Regular.ttf" }) {
            "Ubuntu fonts required for LaTeX PDF. Add static/fonts/Ubuntu-Regular.ttf to resources."
        }
        return texapiClient.compileFilesToPdf(latex, fontFiles)
    }

    private fun renderTemplate(context: Map<String, Any>, templateName: String = "team-form.tex"): String {
        val templateSource = ClassPathResource("templates/latex/$templateName").inputStream.reader().readText()
        return Mustache.compiler()
            .withDelims("<% %>")
            .escapeHTML(false)
            .compile(templateSource)
            .execute(context)
    }

    private fun formatScore(value: Double?): String {
        if (value == null) return "---"
        return "%.2f".format(Locale.ENGLISH, value)
    }

    private fun rawFormToMustacheContext(data: RawTeamForm): Map<String, Any> {
        return mapOf(
            "teamName" to escapeLatex(data.teamName),
            "cityName" to escapeLatex(data.cityName),
            "problem" to data.problem.toString(),
            "age" to data.age.toString(),
            "performanceAt" to data.performanceAt,
            "performanceTime" to data.performanceTime,
            "dtEntries" to data.dtEntries.map { e ->
                mapOf(
                    "name" to escapeLatex(e.name),
                    "score" to if (e.isSectionHeader) "---" else formatScore(e.averageScore),
                    "noElement" to e.noElement,
                    "isSectionHeader" to e.isSectionHeader
                )
            },
            "dtSum" to formatScore(data.dtSum),
            "styleEntries" to data.styleEntries.map { e ->
                mapOf(
                    "name" to escapeLatex(e.name),
                    "score" to formatScore(e.averageScore),
                    "noElement" to e.noElement,
                    "isSectionHeader" to false
                )
            },
            "styleSum" to formatScore(data.styleSum),
            "penaltyEntries" to data.penaltyEntries.map { e ->
                mapOf(
                    "name" to escapeLatex(e.name),
                    "score" to formatScore(e.averageScore),
                    "noElement" to e.noElement,
                    "isSectionHeader" to false
                )
            },
            "penaltySum" to formatScore(data.penaltySum),
            "totalSum" to formatScore(data.totalSum)
        )
    }

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

    private fun loadFontFiles(): List<Pair<String, ByteArray>> {
        val fontNames = listOf("Ubuntu-Regular.ttf", "Ubuntu-Bold.ttf", "Ubuntu-Italic.ttf", "Ubuntu-BoldItalic.ttf")
        return fontNames.mapNotNull { name ->
            val resource = ClassPathResource("static/fonts/$name")
            if (!resource.exists()) return@mapNotNull null
            resource.inputStream.use { it.readBytes() }.takeIf { it.isNotEmpty() }?.let { name to it }
        }
    }
}
