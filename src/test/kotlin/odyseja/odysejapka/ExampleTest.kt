package odyseja.odysejapka

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMINISTRATION"])
class ExampleTest : OdysejaDsl() {
    @Test
    fun `example test`() {

    }
}
