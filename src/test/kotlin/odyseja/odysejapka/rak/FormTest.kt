package odyseja.odysejapka.rak

import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.city.CreateCityRequest
import odyseja.odysejapka.form.FormEntry
import odyseja.odysejapka.form.ProblemForm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormTest : OdysejaDsl() {

    @Test
    fun `should set form entry`() {
        seedDefault()

        val entries = form()
        assertThat(entries.dtEntries).hasSize(1)
        assertThat(entries.dtEntries[0].calcType).isEqualTo(FormEntry.CalcType.AVERAGE)
        assertThat(entries.styleEntries[0].name).isEqualTo("Style")
    }

    @Test
    fun `should update entry`() {
        seedDefault()
        val existing = form()

        val dtId = existing.dtEntries.first().id
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = listOf(FormEntry(dtId, "DT new", FormEntry.CalcType.SUM)),
            style = listOf(FormEntry(styleId, "Style", FormEntry.CalcType.SUM)),
            penalty = listOf(FormEntry(penaltyId, "Penalty", FormEntry.CalcType.SUM))
        )

        val updated = form()
        assertThat(updated.dtEntries).hasSize(1)
        assertThat(updated.dtEntries[0].name).isEqualTo("DT new")
        assertThat(updated.dtEntries[0].calcType).isEqualTo(FormEntry.CalcType.SUM)
    }

    @Test
    fun `should delete entry when omitted from request`() {
        seedDefault()
        val existing = form()
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = emptyList(),
            style = listOf(FormEntry(styleId, "Style", FormEntry.CalcType.SUM)),
            penalty = listOf(FormEntry(penaltyId, "Penalty", FormEntry.CalcType.SUM))
        )

        val afterDelete = form()
        assertThat(afterDelete.dtEntries).isEmpty()
        assertThat(afterDelete.styleEntries).hasSize(1)
        assertThat(afterDelete.styleEntries[0].name).isEqualTo("Style")
        assertThat(afterDelete.penaltyEntries).hasSize(1)
        assertThat(afterDelete.penaltyEntries[0].name).isEqualTo("Penalty")
    }

    @Test
    fun `should add new entry to existing category`() {
        seedDefault()
        val existing = form()
        val dtId = existing.dtEntries.first().id
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = listOf(FormEntry(dtId, "DT", FormEntry.CalcType.AVERAGE)),
            style = listOf(
                FormEntry(styleId, "Style", FormEntry.CalcType.SUM),
                FormEntry(null, "Style 2", FormEntry.CalcType.SUM)
            ),
            penalty = listOf(FormEntry(penaltyId, "Penalty", FormEntry.CalcType.SUM))
        )

        val afterAdd = form()
        assertThat(afterAdd.styleEntries).hasSize(2)
        assertThat(afterAdd.styleEntries.map { it.name })
            .containsExactlyInAnyOrder("Style", "Style 2")

        assertThat(afterAdd.dtEntries).hasSize(1)
        assertThat(afterAdd.dtEntries[0].name).isEqualTo("DT")
        assertThat(afterAdd.dtEntries[0].calcType).isEqualTo(FormEntry.CalcType.AVERAGE)

        assertThat(afterAdd.penaltyEntries).hasSize(1)
        assertThat(afterAdd.penaltyEntries[0].name).isEqualTo("Penalty")
    }

    @Test
    fun `should set judges count for city`() {
        val city1 = cityClient.saveCity(CreateCityRequest("City 1"))
        val city2 = cityClient.saveCity(CreateCityRequest("City 2"))

        formClient.setJudgesCount(PROBLEM_ID, city1.id, 5)
        formClient.setJudgesCount(PROBLEM_ID, city2.id, 3)

        val city1Judges = formClient.getJudgeCount(PROBLEM_ID, city1.id)
        val city2Judges = formClient.getJudgeCount(PROBLEM_ID, city2.id)

        assertThat(city1Judges).isEqualTo(5)
        assertThat(city2Judges).isEqualTo(3)
    }
}
