package odyseja.odysejapka.rak

import Team
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import odyseja.odysejapka.pdf.LatexRequest
import odyseja.odysejapka.pdf.PdfService
import odyseja.odysejapka.problem.ProblemService
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import java.io.ByteArrayOutputStream

@Service
class PdfGeneratorService(
    private val htmlGeneratorService: HtmlGeneratorService,
    private val pdfService: PdfService,
    private val problemService: ProblemService,
    private val templateEngine: TemplateEngine
) {

    fun generateShortPdf(teams: List<Team>): ByteArray {
        val html = htmlGeneratorService.generateShortResults(teams)
        return convertHtmlToPdf(html)
    }

    fun generatePdf(teams: List<Team>): ByteArray {
        val context = ResultsContextBuilder(teams, problemService.getProblems()).full()
        val generated = templateEngine.process("results.latex", context)
        return pdfService.generate(
            LatexRequest(generated)
        )!!
    }

    fun convertHtmlToPdf(html: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val builder = PdfRendererBuilder()

        builder.withHtmlContent(html, null)
        builder.toStream(outputStream)
        builder.useFastMode()
        builder.useFont({ this.javaClass.getResourceAsStream("/static/fonts/Ubuntu-Regular.ttf") }, "Ubuntu")
        builder.withHtmlContent(html, null)

        builder.run()

        return outputStream.toByteArray()
    }
}