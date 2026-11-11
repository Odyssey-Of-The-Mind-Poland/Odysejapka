package odyseja.odysejapka.spontan

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMINISTRATOR"])
class FormulaCalculatorTest : OdysejaDsl() {

    @Test
    fun `should calculate formula`() {
        val formula = "v*2"
        val result = spontanClient.testFormula(TestFormulaRequest(formula, 3.0))
        assertThat(result.success).isTrue
        assertThat(result.result).isEqualTo(6.0)
    }

    @Test
    fun `should reject invalid formula`() {
        val formula = "dupa"
        val result = spontanClient.testFormula(TestFormulaRequest(formula, 3.0))
        assertThat(result.success).isFalse
    }
}