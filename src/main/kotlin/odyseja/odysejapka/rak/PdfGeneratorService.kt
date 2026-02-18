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

    fun generateShortPdf(teams: List<Team>, isRegion: Boolean = false): ByteArray {
        val html = htmlGeneratorService.generateShortResults(teams, isRegion)
        return convertHtmlToPdf(html)
    }

    fun generatePdf(teams: List<Team>, isRegion: Boolean = false): ByteArray {
        val html = htmlGeneratorService.generateHtmlResults(teams, isRegion)
        return convertHtmlToPdf(html)
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