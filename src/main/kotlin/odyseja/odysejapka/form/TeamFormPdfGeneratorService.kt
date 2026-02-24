package odyseja.odysejapka.form

import org.springframework.stereotype.Service

@Service
class TeamFormPdfGeneratorService(
    private val latexGeneratorService: TeamFormLatexGeneratorService
) {

    fun generatePdf(performanceId: Int): ByteArray {
        return latexGeneratorService.generatePdf(performanceId)
    }
}

