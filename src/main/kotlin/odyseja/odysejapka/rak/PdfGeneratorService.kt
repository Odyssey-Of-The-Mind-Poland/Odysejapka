package odyseja.odysejapka.rak

import Team
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import odyseja.odysejapka.problem.ProblemService
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class PdfGeneratorService(
    private val htmlGeneratorService: HtmlGeneratorService
) {

    fun generatePdf(teams: List<Team>): ByteArray {
        var html = htmlGeneratorService.generateHtmlResults(teams)
        return convertHtmlToPdf(html)
    }

    fun convertHtmlToPdf(html: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val builder = PdfRendererBuilder()

        builder.withHtmlContent(html, null)
        builder.toStream(outputStream)

        builder.run()

        return outputStream.toByteArray()
    }
}