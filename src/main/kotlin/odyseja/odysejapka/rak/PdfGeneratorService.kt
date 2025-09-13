package odyseja.odysejapka.rak

import Team
import odyseja.odysejapka.pdf.LatexRequest
import odyseja.odysejapka.pdf.PdfService
import odyseja.odysejapka.problem.ProblemService
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine

@Service
class PdfGeneratorService(
    private val pdfService: PdfService,
    private val problemService: ProblemService,
    private val templateEngine: TemplateEngine
) {

    companion object {
        const val RESULTS_TEMPLATE = "results.latex"
    }

    fun generateShortPdf(teams: List<Team>): ByteArray {
        val context = ResultsContextBuilder(teams, problemService.getProblems()).short()
        val generated = templateEngine.process(RESULTS_TEMPLATE, context)
        return pdfService.generate(
            LatexRequest(generated)
        )!!
    }

    fun generatePdf(teams: List<Team>): ByteArray {
        val context = ResultsContextBuilder(teams, problemService.getProblems()).full()
        val generated = templateEngine.process(RESULTS_TEMPLATE, context)
        return pdfService.generate(
            LatexRequest(generated)
        )!!
    }
}