package odyseja.odysejapka.timetable

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import java.time.LocalDate
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class ImportCsvTest: OdysejaDsl() {

    @Test
    fun `should import csv file`() {
        val city = createCity("finał")
        val content = this.javaClass.getResourceAsStream("/import-csv-test-cases/fo2025.csv")
            ?: throw IllegalArgumentException("Brakuje pliku fo2025.csv.")
        val csvFile = MockMultipartFile(
            "file",
            "fo2025.csv",
            "text/csv",
            content
        )
        timeTableClient.importPerformances(csvFile, city.id)

        Assertions.assertThat(timeTableClient.getPerformances(city.id)).hasSize(280)

        // val mockPerformance = PerformanceEntity()
        val realPerformance = timeTableClient.getPerformance(1)
        val mockPerformance = Performance(
            id = 1,
            city = "finał",
            team = "Zespół Szkół Zawodowych i Ogólnokształcących - Kartuzy",
            problem = 3,
            age = 3,
            stage = 1,
            performance = "8:25",
            spontan = "12:45",
            part = null,
            performanceDay = "sobota",
            spontanDay = "niedziela",
            league = "",
            zspRow = null,
            zspSheet = null,
            performanceDate = LocalDate.of(2025, 4, 5)
        )

        Assertions.assertThat(realPerformance).isEqualTo(mockPerformance)
    }
}