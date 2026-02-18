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
            performance = "08:25",
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
    fun `should reject invalid inputs`() {
        val city = createCity("finał")

        val badPerformanceDay = mockCsv(performanceDay = "wtorek")
        val badSpontanDay = mockCsv(spontanDay = "2")
        val badProblem = mockCsv(problem = 6)
        val badAge = mockCsv(age = 5)
        val badPerformance = mockCsv(performance = "12 34")
        val badSpontan = mockCsv(spontan = "00:000")
        val badName = mockCsv(name = "")

        Assertions.assertThatIllegalArgumentException().isThrownBy {
            timeTableClient.importPerformances(badPerformanceDay, city.id)
        }
            .withMessageContaining("Dozwolone dni występu to sobota lub niedziela.")
        Assertions.assertThatIllegalArgumentException().isThrownBy {
            timeTableClient.importPerformances(badSpontanDay, city.id)
        }
            .withMessageContaining("Dozwolone dni spontana to sobota lub niedziela.")
        Assertions.assertThatIllegalArgumentException().isThrownBy {
            timeTableClient.importPerformances(badProblem, city.id)
        }
            .withMessageContaining("Numer problemu musi wynosić od 0 do 5.")
        Assertions.assertThatIllegalArgumentException().isThrownBy {
            timeTableClient.importPerformances(badAge, city.id)
        }
            .withMessageContaining("Numer grupy wiekowej musi wynosić od 0 do 4.")
        Assertions.assertThatIllegalArgumentException().isThrownBy {
            timeTableClient.importPerformances(badPerformance, city.id)
        }
            .withMessageContaining("Godzina występu powinna być w następującym formacie: 08:45.")
        Assertions.assertThatIllegalArgumentException().isThrownBy {
            timeTableClient.importPerformances(badSpontan, city.id)
        }
            .withMessageContaining("Godzina spontana powinna być w następującym formacie: 08:45.")
        Assertions.assertThatIllegalArgumentException().isThrownBy {
            timeTableClient.importPerformances(badName, city.id)
        }
            .withMessageContaining("Nazwa drużyny nie może być pusta.")
    }
}