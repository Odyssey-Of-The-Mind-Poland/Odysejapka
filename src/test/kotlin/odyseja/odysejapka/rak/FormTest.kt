package odyseja.odysejapka.rak

import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.form.FormEntry
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMINISTRATOR"])
class FormTest : OdysejaDsl() {

    @Test
    fun `should set form entry`() {
        formClient.setFormEntries(
            1,
            listOf(FormEntry(0, "Label", FormEntry.CalcType.SUM, FormEntry.FormCategory.DT))
        )
        val entries = formClient.getFormEntries(1)
        assert(entries.size == 1)
    }
}