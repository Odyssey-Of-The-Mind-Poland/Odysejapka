package odyseja.odysejapka

import com.fasterxml.jackson.databind.ObjectMapper
import odyseja.odysejapka.city.CityController
import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CreateCityRequest
import odyseja.odysejapka.city.KonkursLevel
import odyseja.odysejapka.form.FormController
import odyseja.odysejapka.form.JudgeType
import odyseja.odysejapka.form.LongTermFormEntry
import odyseja.odysejapka.form.StyleFormEntry
import odyseja.odysejapka.form.PenaltyFormEntry
import odyseja.odysejapka.form.PerformanceResultsRequest
import odyseja.odysejapka.form.FormData
import odyseja.odysejapka.roles.Role
import odyseja.odysejapka.spontan.SpontanController
import odyseja.odysejapka.timetable.Performance
import odyseja.odysejapka.timetable.TimeTableController
import odyseja.odysejapka.users.CreateUserRequest
import odyseja.odysejapka.users.UserRoles
import odyseja.odysejapka.users.UserService
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.http.ProblemDetail
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory
import ovh.snet.grzybek.controller.client.core.ControllerResponse
import java.time.LocalDate

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@AutoConfigureMockMvc
@ComponentScan(basePackages = ["odyseja.odysejapka"])
@ActiveProfiles("test")
class OdysejaDsl {

    lateinit var formClient: FormController
    lateinit var cityClient: CityController
    lateinit var timeTableClient: TimeTableController
    lateinit var spontanClient: SpontanController

    @Autowired
    lateinit var controllerClientFactory: ControllerClientFactory

    @Autowired
    lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        ensureTestUserExists()
        formClient = controllerClientFactory.create(FormController::class.java)
        cityClient = controllerClientFactory.create(CityController::class.java)
        timeTableClient = controllerClientFactory.create(TimeTableController::class.java)
        spontanClient = controllerClientFactory.create(SpontanController::class.java)
    }

    private fun ensureTestUserExists() {
        val testUserId = "testuser"
        if (userService.getUserByUserId(testUserId) == null) {
            val user = userService.createUser(
                CreateUserRequest(
                    username = testUserId,
                    email = "testuser@test",
                    userId = testUserId
                )
            )
            userService.assignRolesToUser(UserRoles(userId = user.id, roles = listOf(Role.ADMINISTRATOR)))
        }
    }


    val PROBLEM_ID = 1

    fun setForm(
        dt: List<LongTermFormEntry>,
        style: List<StyleFormEntry>,
        penalty: List<PenaltyFormEntry>
    ) {
        formClient.setFormData(PROBLEM_ID, FormData(dt, style, penalty))
    }

    fun form() = formClient.getFormData(PROBLEM_ID)

    fun seedDefault(): Unit = setForm(
        dt = listOf(LongTermFormEntry(
            id = null, name = "DT", type = LongTermFormEntry.EntryType.SCORING,
            scoring = LongTermFormEntry.ScoringData(
                scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                noElementEnabled = false
            )
        )),
        style = listOf(StyleFormEntry(
            id = null, name = "Style", type = StyleFormEntry.EntryType.STYLE
        )),
        penalty = listOf(PenaltyFormEntry(
            id = null, name = "Penalty", type = PenaltyFormEntry.EntryType.PENALTY
        ))
    )

    fun seedDefaultFormWithIds(): Triple<Long, Long, Long> {
        seedDefault()
        val entries = form()
        val dtId = entries.dtEntries.first().id!!
        val styleId = entries.styleEntries.first().id!!
        val penaltyId = entries.penaltyEntries.first().id!!
        return Triple(dtId, styleId, penaltyId)
    }

    fun createCity(name: String, level: KonkursLevel = KonkursLevel.FINAL): CityEntity {
        return cityClient.saveCity(CreateCityRequest(name, level))
    }

    fun getCityByName(name: String) = cityClient.getCities().firstOrNull { it?.name == name }

    fun createPerformance(cityId: Int): Int {
        return createPerformance(cityId, team = "Sample Team")
    }

    fun createPerformance(
        cityId: Int,
        team: String = "Sample Team",
        problem: Int = 1,
        age: Int = 1,
        stage: Int = 1,
        spontan: String = "11:11",
        spontanDay: String = "sobota",
        performance: String = "11:11",
        performanceDay: String = "niedziela",
        league: String? = ""
    ): Int {
        val cityName = cityClient.getCities().firstOrNull { it?.id == cityId }?.name ?: "Unknown"
        val perf = Performance(
            city = cityName, team = team,
            id = 0,
            problem = problem,
            age = age,
            stage = stage,
            performance = performance,
            spontan = spontan,
            part = 1,
            performanceDay = performanceDay,
            spontanDay = spontanDay,
            league = league,
            zspRow = 1,
            zspSheet = "",
            performanceDate = LocalDate.now(),
        )
        return timeTableClient.addPerformance(perf).id
    }

    fun getTeamResults(performanceId: Int) = formClient.getTeamResults(performanceId, null)

    fun setTeamResults(performanceId: Int, results: List<PerformanceResultsRequest.PerformanceResult>) {
        formClient.setTeamResults(performanceId, PerformanceResultsRequest(results), null)
    }

    fun performanceResult(entryId: Long, result: Long, judge: Int = 1, judgeType: JudgeType = JudgeType.DT_A) =
        PerformanceResultsRequest.PerformanceResult(entryId = entryId, judgeType = judgeType, judge = judge, result = result)

    fun mockCsv(
        performanceDay: Any = "sobota",
        spontanDay: Any = "niedziela",
        problem: Any = 1,
        age: Any = 1,
        performance: Any = "8:45",
        spontan: Any = "10:00",
        name: Any = "Szkoła Podstawowa nr 0 - Gdańsk",
        stage: Any = 1
    ): MockMultipartFile {
        val content = """
            Konkurs,Scena,Dzień występu,Dzień spontana,Problem,Grupa wiekowa,Liga,Część,Godzina występu,Godzina spontana,Kod drużyny,Numer członkostwa,Drużyna,Miejscowość
            finał,$stage,$performanceDay,$spontanDay,$problem,$age,,,$performance,$spontan,P1G1_1,11111,$name,Miejscowość
        """.trimIndent()

        val csvFile = MockMultipartFile(
            "file",
            "fo2025.csv",
            "text/csv",
            content.toByteArray())

        return csvFile
    }

    fun parseProblemDetail(response: ControllerResponse<Void>): ProblemDetail {
        val mapper = ObjectMapper()
        return mapper.readValue(
            response.mockHttpServletResponse.contentAsString,
            ProblemDetail::class.java
        )
    }
}
