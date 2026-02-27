package odyseja.odysejapka.form

import org.springframework.stereotype.Service

@Service
class TeamFormPdfGeneratorService(
    private val latexGeneratorService: TeamFormLatexGeneratorService
) {

    fun generatePdf(performanceId: Int, english: Boolean = false): ByteArray {
        return latexGeneratorService.generatePdf(performanceId, english)
    }
}

