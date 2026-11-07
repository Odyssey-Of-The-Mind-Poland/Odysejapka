package odyseja.odysejapka.stage

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient
import java.util.UUID

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class StageStyleTest : OdysejaDsl() {

    lateinit var stageClient: StageController
    lateinit var stageRespondingClient: RespondingControllerClient<StageController>

    @BeforeEach
    fun stageStyleSetUp() {
        stageClient = controllerClientFactory.create(StageController::class.java)
        stageRespondingClient = controllerClientFactory.respondingClient(StageController::class.java)
    }

    @Test
    fun `should store the icon and colour of a stage and clear them`() {
        val city = createCity("Sceny ${UUID.randomUUID().toString().take(6)}")

        stageClient.updateStage(listOf(Stage(0, 1, city.id, "Aula", "building-02", "green"), Stage(0, 2, city.id, "Hala")))

        val stages = stageClient.getStages(city.id).associateBy { it.number }
        assertThat(stages.getValue(1).icon).isEqualTo("building-02")
        assertThat(stages.getValue(1).color).isEqualTo("green")
        assertThat(stages.getValue(2).icon).isNull()
        assertThat(stages.getValue(2).color).isNull()

        stageClient.updateStage(listOf(Stage(0, 1, city.id, "Aula")))

        val cleared = stageClient.getStages(city.id).single { it.number == 1 }
        assertThat(cleared.icon).isNull()
        assertThat(cleared.color).isNull()
    }

    @Test
    fun `should reject an unknown colour or a malformed icon`() {
        val city = createCity("Sceny ${UUID.randomUUID().toString().take(6)}")

        val badColor = parseProblemDetail(stageRespondingClient.executeConsumer {
            it.updateStage(listOf(Stage(0, 1, city.id, "Aula", "building-02", "pink")))
        })
        val badIcon = parseProblemDetail(stageRespondingClient.executeConsumer {
            it.updateStage(listOf(Stage(0, 1, city.id, "Aula", "Building 02", "green")))
        })

        assertThat(badColor.status).isEqualTo(400)
        assertThat(badColor.title).isEqualTo("VALIDATION FAILED")
        assertThat(badColor.detail).contains("pink")
        assertThat(badIcon.status).isEqualTo(400)
        assertThat(badIcon.detail).contains("Building 02")
    }
}
