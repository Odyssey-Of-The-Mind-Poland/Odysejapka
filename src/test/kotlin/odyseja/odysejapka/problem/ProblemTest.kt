package odyseja.odysejapka.problem

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient
import kotlin.test.Test

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class ProblemTest: OdysejaDsl() {
    lateinit var problemClient: ProblemController
    lateinit var problems: List<ProblemEntity>
    lateinit var problemRespondingClient: RespondingControllerClient<ProblemController>

    @BeforeEach
    fun problemTestSetUp() {
        problemClient = controllerClientFactory.create(ProblemController::class.java)
        problemRespondingClient = controllerClientFactory.respondingClient(ProblemController::class.java)
        problems = mutableListOf(
            ProblemEntity(1, "Taka kolej rzeczy"),
            ProblemEntity(2, "Tędy przez błędy"),
            ProblemEntity(3, "To będzie epickie!"),
            ProblemEntity(4, "Balanga na okrągło"),
            ProblemEntity(5, "Opowieść przeniesamowita"),
            ProblemEntity(0, "Nauka na wesoło")
        )
    }

    @Test
    fun `should update problems`() {
        problemClient.updateProblems(problems)
        val updatedProblems = problemClient.getProblems()

        Assertions.assertThat(updatedProblems.size == 6)

        val problem1 = updatedProblems.firstOrNull {it?.id == 1}
        Assertions.assertThat(problem1).isNotNull
        Assertions.assertThat(problem1!!.name).isEqualTo("Taka kolej rzeczy")

        val overwriteExisting = listOf(
            ProblemEntity(1, "Wymyślny wyścig"),
            ProblemEntity(2, "Zwierzątko-robociątko"),
            ProblemEntity(3, "Komunikacja od kuchni"),
            ProblemEntity(4, "Bezpieczne balschronienie"),
            ProblemEntity(5, "Historia pełna gwiazd"),
            ProblemEntity(0, "Czas na cuda")
        )

        Assertions.assertThatNoException().isThrownBy { problemClient.updateProblems(overwriteExisting) }
        Assertions.assertThat(problemClient.getProblems().size).isEqualTo(6)

        val newProblem1 = problemClient.getProblems().firstOrNull {it?.id == 1}
        Assertions.assertThat(newProblem1).isNotNull
        Assertions.assertThat(newProblem1!!.name).isEqualTo("Wymyślny wyścig")
    }

    @Test
    fun `should delete problems`() {
        problemClient.updateProblems(problems)
        problemClient.deleteProblem(1)

        Assertions.assertThat(problemClient.getProblems()).hasSize(5)
        Assertions.assertThat(problemClient.getProblems().firstOrNull {it?.id == 1}).isNull()
    }

    @Test
    fun `should create problem if it's missing`() {
        timeTableClient.clearTimetable()
        problemClient.updateProblems(problems)
        problemClient.deleteProblem(1)
        val city = createCity("Finał")

        createPerformance(city.id, problem = 1)
        Assertions.assertThat(problemClient.getProblems()).hasSize(6)

        val problem1 = problemClient.getProblems().firstOrNull {it?.id == 1}
        Assertions.assertThat(problem1).isNotNull
        Assertions.assertThat(problem1!!.name).isEqualTo("Problem 1")
    }

    @Test
    fun `should reject invalid problems`() {
        var response = problemRespondingClient.executeConsumer { controller ->
            controller.updateProblems(listOf(
                ProblemEntity(6, "Problem nieistniejący")
            ))
        }
        var detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(400)
        Assertions.assertThat(detail.detail).isEqualTo("Numer problemu musi wynosić od 0 do 5")
        Assertions.assertThat(detail.title).isEqualTo("ILLEGAL ARGUMENT")

        response = problemRespondingClient.executeConsumer { controller ->
            controller.updateProblems(listOf(
                ProblemEntity(0, "")
            ))
        }
        detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(400)
        Assertions.assertThat(detail.detail).isEqualTo("Nazwa problemu nie może być pusta")
        Assertions.assertThat(detail.title).isEqualTo("ILLEGAL ARGUMENT")
    }

    @Test
    fun `should update problem tied to performance`() {
        timeTableClient.clearTimetable()
        val city = createCity("Finał")
        createPerformance(city.id, problem = 1)
        Assertions.assertThatNoException().isThrownBy {
            problemClient.updateProblems(listOf(
                ProblemEntity(1, "Aktualizacja problemu powinna się udać")
            ))
        }
    }

    @Test
    fun `should fail to delete problem tied to performance`() {
        // Wydaje mi się, że nie powinno dać się usuwać przedstawień przez problem, w przeciwieństwie do miast.
        // Nie widzę dla tego żadnego zastosowania, a potencjalne wpadki już tak.

        timeTableClient.clearTimetable()
        val city = createCity("Finał")
        createPerformance(city.id, problem = 1)

        val response = problemRespondingClient.executeConsumer { controller ->
            controller.deleteProblem(1)
        }
        val detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(409)
        Assertions.assertThat(detail.title).isEqualTo("DATA INTEGRITY VIOLATION")

    }
}