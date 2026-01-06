package odyseja.odysejapka

import odyseja.odysejapka.city.CityController
import odyseja.odysejapka.form.CalcType
import odyseja.odysejapka.form.FormController
import odyseja.odysejapka.form.FormEntry
import odyseja.odysejapka.form.ProblemForm
import odyseja.odysejapka.timetable.TimeTableController
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory

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
        dt: List<FormEntry>,
        style: List<FormEntry>,
        penalty: List<FormEntry>
    ) {
        formClient.setProblemForm(PROBLEM_ID, ProblemForm(dt, style, penalty))
    }

    fun form() = formClient.getProblemForm(PROBLEM_ID)

    fun seedDefault(): Unit = setForm(
        dt = listOf(FormEntry(
            null, "DT", FormEntry.EntryType.PUNCTUATION,
            punctuation = FormEntry.PunctuationData(
                punctuationType = FormEntry.PunctuationType.SUBJECTIVE,
                pointsMin = 0,
                pointsMax = 100,
                judges = FormEntry.JudgeType.A,
                noElement = false
            )
        )),
        style = listOf(FormEntry(
            null, "Style", FormEntry.EntryType.PUNCTUATION,
            punctuation = FormEntry.PunctuationData(
                punctuationType = FormEntry.PunctuationType.SUBJECTIVE,
                pointsMin = 0,
                pointsMax = 50,
                judges = FormEntry.JudgeType.B,
                noElement = false
            )
        )),
        penalty = listOf(FormEntry(
            null, "Penalty", FormEntry.EntryType.PUNCTUATION,
            punctuation = FormEntry.PunctuationData(
                punctuationType = FormEntry.PunctuationType.OBJECTIVE,
                pointsMin = 0,
                pointsMax = 10,
                judges = FormEntry.JudgeType.A,
                noElement = false
            )
        ))
    )

}
