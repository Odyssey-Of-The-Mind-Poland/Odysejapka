package odyseja.odysejapka.breaking.change

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class BreakingChangeTest: OdysejaDsl() {
    lateinit var breakingChangeClient: BreakingChangeController
    lateinit var breakingChangeRespondingControllerClient: RespondingControllerClient<BreakingChangeController>

    @BeforeEach
    fun breakingChangeTestSetUp() {
        breakingChangeClient = controllerClientFactory.create(BreakingChangeController::class.java)
        breakingChangeRespondingControllerClient = controllerClientFactory.respondingClient(BreakingChangeController::class.java)
    }

    @Test
    fun `should return a default version of 0 if there is no breaking change`() {
        val lastBreakingChange = breakingChangeClient.getLastBreakingChange()

        Assertions.assertThat(lastBreakingChange.version).isEqualTo("0.0.0")
    }

    @Test
    fun `should set and fetch breaking changes`() {
        breakingChangeClient.setBreakingChange(BreakingChange("0.0.1"))
        Assertions.assertThat(breakingChangeClient.getLastBreakingChange().version).isEqualTo("0.0.1")

        breakingChangeClient.setBreakingChange(BreakingChange("1.3.2+23"))
        Assertions.assertThat(breakingChangeClient.getLastBreakingChange().version).isEqualTo("1.3.2+23")

        breakingChangeClient.setBreakingChange(BreakingChange("0.0.0"))
        Assertions.assertThat(breakingChangeClient.getLastBreakingChange().version).isEqualTo("0.0.0")
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.2.3", "1.2", "1", "1.2.3.4", "1.2.3333", "1.2.3+45", "1+23"])
    fun `should accept non-obvious inputs`(testCase: String) {
        breakingChangeClient.setBreakingChange(BreakingChange(testCase))
        Assertions.assertThat(breakingChangeClient.getLastBreakingChange().version).isEqualTo(testCase)
    }

    @ParameterizedTest
    @ValueSource(strings = ["zero.zero.zero", ".", "...", "v1.2", "1.2.3-alpha", "omer na hulajnodze", "",
        "1.2.", ".1.2", "1+2.3", "1+2+3", "1.2+3.4", "1.2.+3", "1.2.3+", "+"])
    fun `should reject non-integer inputs`(testCase: String) {
        val response = breakingChangeRespondingControllerClient.executeConsumer { controller ->
                controller.setBreakingChange(BreakingChange(testCase))
            }
        val detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(400)
        Assertions.assertThat(detail.detail)
        Assertions.assertThat(detail.title).isEqualTo("ILLEGAL ARGUMENT")
    }

    @Test
    fun `should properly display error messages`() {
        val nonInteger = "nieliczba"
        var response = breakingChangeRespondingControllerClient.executeConsumer { controller ->
            controller.setBreakingChange(BreakingChange(nonInteger))
        }
        var detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(400)
        Assertions.assertThat(detail.detail)
            .isEqualTo("Wersja musi być w formacie W.X.Y+Z, gdzie W, X, Y i Z są liczbami (Int)")
        Assertions.assertThat(detail.title).isEqualTo("ILLEGAL ARGUMENT")

        val invalidBuildNumber = "1+2+3"
        response = breakingChangeRespondingControllerClient.executeConsumer { controller ->
            controller.setBreakingChange(BreakingChange(invalidBuildNumber))
        }
        detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(400)
        Assertions.assertThat(detail.detail)
            .isEqualTo("Wersja nie może mieć więcej niż 1 build number")
        Assertions.assertThat(detail.title).isEqualTo("ILLEGAL ARGUMENT")


    }

    @Test
    fun `should show when there is a breaking change available`() {
        var currentVersion = "0.0.1"
        breakingChangeClient.setBreakingChange(BreakingChange("0.0.5"))
        Assertions.assertThat(breakingChangeClient.shouldUpdate(currentVersion)).isTrue()

        currentVersion = "0.0.6"
        Assertions.assertThat(breakingChangeClient.shouldUpdate(currentVersion)).isFalse()

        breakingChangeClient.setBreakingChange(BreakingChange("0.1.0"))
        Assertions.assertThat(breakingChangeClient.shouldUpdate(currentVersion)).isTrue()

        currentVersion = "0.1.19"
        breakingChangeClient.setBreakingChange(BreakingChange("0.1.2"))
        Assertions.assertThat(breakingChangeClient.shouldUpdate(currentVersion)).isFalse()

        currentVersion = "2.1.2.4"
        breakingChangeClient.setBreakingChange(BreakingChange("2.1.4"))
        Assertions.assertThat(breakingChangeClient.shouldUpdate(currentVersion)).isTrue()

        currentVersion = "2.1.4+33"
        breakingChangeClient.setBreakingChange(BreakingChange("2.1.4+34"))
        Assertions.assertThat(breakingChangeClient.shouldUpdate(currentVersion)).isTrue()
        breakingChangeClient.setBreakingChange(BreakingChange("2.1.4+33"))
        Assertions.assertThat(breakingChangeClient.shouldUpdate(currentVersion)).isFalse()
        breakingChangeClient.setBreakingChange(BreakingChange("2.1.4+32"))
        Assertions.assertThat(breakingChangeClient.shouldUpdate(currentVersion)).isFalse()
    }

    @Test
    fun `should reject invalid inputs in shouldUpdate`() {
        val currentVersion = "nieliczba"
        val response = breakingChangeRespondingControllerClient.executeConsumer { controller ->
            controller.shouldUpdate(currentVersion)
        }
        val detail = parseProblemDetail(response)
        Assertions.assertThat(detail.status).isEqualTo(400)
    }
}