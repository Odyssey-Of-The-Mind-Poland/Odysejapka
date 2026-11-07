package odyseja.odysejapka.timetable

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser
import ovh.snet.grzybek.controller.client.core.RespondingControllerClient

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class AgeTest : OdysejaDsl() {

    lateinit var ageClient: AgeController
    lateinit var ageRespondingClient: RespondingControllerClient<AgeController>

    @BeforeEach
    fun ageSetUp() {
        ageClient = controllerClientFactory.create(AgeController::class.java)
        ageRespondingClient = controllerClientFactory.respondingClient(AgeController::class.java)
        ageClient.updateAges((0..4).map { Age(it) })
    }

    @Test
    fun `should return all five age groups, unstyled by default`() {
        val ages = ageClient.getAges()

        assertThat(ages.map { it.id }).containsExactly(0, 1, 2, 3, 4)
        assertThat(ages).allSatisfy {
            assertThat(it.icon).isNull()
            assertThat(it.color).isNull()
        }
    }

    @Test
    fun `should store the style of an age group`() {
        ageClient.updateAges(listOf(Age(2, "users-01", "purple"), Age(0, "star-01", "red")))

        val ages = ageClient.getAges().associateBy { it.id }
        assertThat(ages.getValue(2).icon).isEqualTo("users-01")
        assertThat(ages.getValue(2).color).isEqualTo("purple")
        assertThat(ages.getValue(0).color).isEqualTo("red")
        assertThat(ages.getValue(1).color).isNull()
    }

    @Test
    fun `should reject an unknown colour, a malformed icon and an age group out of range`() {
        val badColor = parseProblemDetail(ageRespondingClient.executeConsumer { it.updateAges(listOf(Age(1, "users-01", "pink"))) })
        val badIcon = parseProblemDetail(ageRespondingClient.executeConsumer { it.updateAges(listOf(Age(1, "Users 01", "red"))) })
        val outOfRange = parseProblemDetail(ageRespondingClient.executeConsumer { it.updateAges(listOf(Age(5, "users-01", "red"))) })

        assertThat(badColor.status).isEqualTo(400)
        assertThat(badColor.title).isEqualTo("VALIDATION FAILED")
        assertThat(badIcon.status).isEqualTo(400)
        assertThat(outOfRange.status).isEqualTo(400)
        assertThat(outOfRange.detail).contains("0, 1, 2, 3 lub 4")
    }
}
