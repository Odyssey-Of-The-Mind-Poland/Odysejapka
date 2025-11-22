package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMINISTRATION"])
class FormTest : OdysejaDsl() {

    @Test
    fun `example form test`() {
        seedDefault()
        val form = form()
        assert(form.dtEntries.size == 1)
    }
}