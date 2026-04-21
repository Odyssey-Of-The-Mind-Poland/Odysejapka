package odyseja.odysejapka.timetable

import com.opencsv.exceptions.CsvRequiredFieldEmptyException
import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.city.CityEntity
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient
import java.time.LocalDate
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMINISTRATOR"])
class ImportCsvTest: OdysejaDsl() {

    private lateinit var city: CityEntity
    lateinit var timetableRespondingClient: RespondingControllerClient<TimeTableController>

    @BeforeEach
    fun csvSetUp() {
        clearCities()
        city = createCity("finał")
        timetableRespondingClient = controllerClientFactory.respondingClient(TimeTableController::class.java)
    }

    @Test
    fun `should import csv file`() {
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

        val realPerformance = timeTableClient.getPerformances(city.id)[0]
        val mockPerformance = Performance(
            id = realPerformance.id,
            city = "finał",
            team = "Zespół Szkół Zawodowych i Ogólnokształcących - Kartuzy",
            problem = 3,
            age = 3,
            stage = 1,
            performance = "08:25",
            spontan = "12:45",
            part = 0,
            performanceDay = "sobota",
            spontanDay = "niedziela",
            league = "",
            zspRow = null,
            zspSheet = null,
            performanceDate = LocalDate.of(2025, 4, 5)
        )

        Assertions.assertThat(realPerformance).isEqualTo(mockPerformance)
    }

    @Test
    fun `should reject imports with improper data`() {
        val testCases = listOf(
            mockCsv(performanceDay = "wtorek") to "Dozwolone dni występu to sobota lub niedziela",
            mockCsv(spontanDay = "2") to "Dozwolone dni spontana to sobota lub niedziela",
            mockCsv(problem = 6) to "Numer problemu musi wynosić od 0 do 5",
            mockCsv(age = 5) to "Dozwolone ID grupy wiekowej to: 0, 1, 2, 3 lub 4",
            mockCsv(performance = "12 34") to "Godzina występu powinna być w następującym formacie: 08:45",
            mockCsv(spontan = "00:000") to "Godzina spontana powinna być w następującym formacie: 08:45"
        )

        testCases.forEach { (content, message) ->
            val response = (timetableRespondingClient.executeConsumer {
                controller -> controller.importPerformances(content, city.id)
            })
            val detail = parseProblemDetail(response)
            Assertions.assertThat(detail.status).isEqualTo(400)
            Assertions.assertThat(detail.detail).isEqualTo(message)
            Assertions.assertThat(detail.title).isEqualTo("ILLEGAL ARGUMENT")
        }
    }

    @Test
    fun `should reject imports with missing headers`() {
        val content = """
            Konkurs,Scena,Dzień występu
            finał,1,sobota
        """.trimIndent()

        val csvFile = MockMultipartFile(
            "file",
            "fo2025.csv",
            "text/csv",
            content.toByteArray())

        Assertions.assertThatThrownBy {
            timeTableClient.importPerformances(csvFile, city.id)
        }
            .hasRootCauseInstanceOf(CsvRequiredFieldEmptyException::class.java)
    }

    @Test
    fun  `should reject files with no data`() {
        val content = "Konkurs,Scena,Dzień występu,Dzień spontana,Problem,Grupa wiekowa,Liga,Część,Godzina występu,Godzina spontana,Kod drużyny,Numer członkostwa,Drużyna,Miejscowość"
        val csvFile = MockMultipartFile(
            "file",
            "fo2025.csv",
            "text/csv",
            content.toByteArray())

        val response = timetableRespondingClient.executeConsumer {
            controller -> controller.importPerformances(csvFile, city.id)}
        val detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(400)
        Assertions.assertThat(detail.detail).isEqualTo("Plik nie zawiera żadnych przedstawień.")
    }

    @Test
    fun `should reject imports with invalid city ID`() {
        val response = timetableRespondingClient.executeConsumer { controller ->
            controller.importPerformances(mockCsv(), 123456789)
        }
        val detail = parseProblemDetail(response)

        Assertions.assertThat(detail.status).isEqualTo(404)
        Assertions.assertThat(detail.title).isEqualTo("ENTITY NOT FOUND")
        Assertions.assertThat(detail.detail).isEqualTo("Nie znaleziono miasta o ID 123456789")
    }

    @Test
    fun `should reject imports with missing required values`() {
        val testCases = listOf(
            mockCsv(name = ""),
            mockCsv(performanceDay = ""),
            mockCsv(spontanDay = ""),
            mockCsv(problem = ""),
            mockCsv(age = ""),
            mockCsv(performance = ""),
            mockCsv(spontan = ""),
            mockCsv(stage = "")
        )

        testCases.forEach {
            Assertions.assertThatThrownBy {
                timeTableClient.importPerformances(it, city.id)
            }
                .hasRootCauseInstanceOf(CsvRequiredFieldEmptyException::class.java)
        }
    }
}
