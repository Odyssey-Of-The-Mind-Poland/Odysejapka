package odyseja.odysejapka.rak

import org.springframework.stereotype.Service

@Service
class MockedPdfService(
    private val pdfGeneratorService: PdfGeneratorService,
    private val csvTeamService: CsvTeamService
) {

    fun generatePdf(): ByteArray {
        val teams = csvTeamService.readTeamsFromCsv("/bialystok2025.csv")
        return pdfGeneratorService.generatePdf(teams)
    }
}