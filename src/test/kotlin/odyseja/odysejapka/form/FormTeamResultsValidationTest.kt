package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormTeamResultsValidationTest : OdysejaDsl() {

    @Test
    fun `should throw for unknown form entry id`() {
        seedDefaultFormWithIds()
        val city = createCity("Opole")
        val perfId = createPerformance(city.id)

        val unknownId = 9_999_999L

        Assertions.assertThatThrownBy {
            setTeamResults(perfId, listOf(performanceResult(unknownId, 1)))
        }
            .hasMessageContaining("Unknown form entry id")
    }
}

