package odyseja.odysejapka.form

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class TeamFormPdfGeneratorService(
    private val htmlGeneratorService: TeamFormHtmlGeneratorService
) {

    fun generatePdf(performanceId: Int): ByteArray {
        val html = htmlGeneratorService.generateHtml(performanceId)
        return convertHtmlToPdf(html)
    }

    private fun convertHtmlToPdf(html: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val builder = PdfRendererBuilder()

        builder.withHtmlContent(html, null)
        builder.toStream(outputStream)
        builder.useFastMode()
        builder.useFont({ this.javaClass.getResourceAsStream("/static/fonts/Ubuntu-Regular.ttf") }, "Ubuntu")

        builder.run()

        return outputStream.toByteArray()
    }
}

