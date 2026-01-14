package odyseja.odysejapka

import odyseja.odysejapka.city.CityController
import odyseja.odysejapka.city.CreateCityRequest
import odyseja.odysejapka.form.FormController
import odyseja.odysejapka.form.JudgeType
import odyseja.odysejapka.form.LongTermFormEntry
import odyseja.odysejapka.form.StyleFormEntry
import odyseja.odysejapka.form.PenaltyFormEntry
import odyseja.odysejapka.form.PerformanceResultsRequest
import odyseja.odysejapka.form.ProblemForm
import odyseja.odysejapka.timetable.Performance
import odyseja.odysejapka.timetable.TimeTableController
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory
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

    @Autowired
    lateinit var controllerClientFactory: ControllerClientFactory

    @BeforeEach
    fun setUp() {
        formClient = controllerClientFactory.create(FormController::class.java)
        cityClient = controllerClientFactory.create(CityController::class.java)
        timeTableClient = controllerClientFactory.create(TimeTableController::class.java)
    }


    val PROBLEM_ID = 1

    fun setForm(
        dt: List<LongTermFormEntry>,
        style: List<StyleFormEntry>,
        penalty: List<PenaltyFormEntry>
    ) {
        formClient.setProblemForm(PROBLEM_ID, ProblemForm(dt, style, penalty))
    }

    fun form() = formClient.getProblemForm(PROBLEM_ID)

    fun seedDefault(): Unit = setForm(
        dt = listOf(LongTermFormEntry(
            null, "DT", LongTermFormEntry.EntryType.SCORING,
            scoring = LongTermFormEntry.ScoringData(
                scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                noElementEnabled = false
            )
        )),
        style = listOf(StyleFormEntry(
            null, "Style", StyleFormEntry.EntryType.STYLE
        )),
        penalty = listOf(PenaltyFormEntry(
            null, "Penalty", PenaltyFormEntry.EntryType.PENALTY
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

    fun createCity(name: String) = cityClient.saveCity(CreateCityRequest(name))

    fun createPerformance(cityId: Int): Int {
        val cityName = cityClient.getCities().firstOrNull { it?.id == cityId }?.name ?: "Unknown"
        val performance = Performance(
            city = cityName, team = "Sample Team",
            id = 0,
            problem = 1,
            age = 1,
            stage = 1,
            performance = "",
            spontan = "",
            part = 1,
            performanceDay = "",
            spontanDay = "",
            league = "",
            zspRow = 1,
            zspSheet = "",
            performanceDate = LocalDate.now(),
        )
        return timeTableClient.addPerformance(performance).id
    }

    fun getTeamResults(performanceId: Int) = formClient.getTeamResults(performanceId)

    fun setTeamResults(performanceId: Int, results: List<PerformanceResultsRequest.PerformanceResult>) {
        formClient.setTeamResults(performanceId, PerformanceResultsRequest(results))
    }

    fun performanceResult(entryId: Long, result: Long, judge: Int = 1, judgeType: JudgeType = JudgeType.DT_A) =
        PerformanceResultsRequest.PerformanceResult(entryId = entryId, judgeType = judgeType, judge = judge, result = result)

}
