package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.city.CreateCityRequest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormTest : OdysejaDsl() {

    @Test
    fun `should set form entry`() {
        seedDefault()

        val entries = form()
        Assertions.assertThat(entries.dtEntries).hasSize(1)
        val dtEntry = entries.dtEntries[0]
        Assertions.assertThat(dtEntry.type).isEqualTo(FormEntry.EntryType.PUNCTUATION)
        Assertions.assertThat(dtEntry.calcType).isEqualTo(CalcType.AVERAGE)
        val styleEntry = entries.styleEntries[0]
        Assertions.assertThat(styleEntry.name).isEqualTo("Style")
    }

    @Test
    fun `should update entry`() {
        seedDefault()
        val existing = form()

        val dtId = existing.dtEntries.first().id
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = listOf(FormEntry(dtId, "DT new", FormEntry.EntryType.PUNCTUATION, CalcType.SUM)),
            style = listOf(FormEntry(styleId, "Style", FormEntry.EntryType.PUNCTUATION, CalcType.SUM)),
            penalty = listOf(FormEntry(penaltyId, "Penalty", FormEntry.EntryType.PUNCTUATION, CalcType.SUM))
        )

        val updated = form()
        Assertions.assertThat(updated.dtEntries).hasSize(1)
        val updatedDt = updated.dtEntries[0]
        Assertions.assertThat(updatedDt.name).isEqualTo("DT new")
        Assertions.assertThat(updatedDt.calcType).isEqualTo(CalcType.SUM)
    }

    @Test
    fun `should delete entry when omitted from request`() {
        seedDefault()
        val existing = form()
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = emptyList(),
            style = listOf(FormEntry(styleId, "Style", FormEntry.EntryType.PUNCTUATION, CalcType.SUM)),
            penalty = listOf(FormEntry(penaltyId, "Penalty", FormEntry.EntryType.PUNCTUATION, CalcType.SUM))
        )

        val afterDelete = form()
        Assertions.assertThat(afterDelete.dtEntries).isEmpty()
        Assertions.assertThat(afterDelete.styleEntries).hasSize(1)
        Assertions.assertThat(afterDelete.styleEntries[0].name).isEqualTo("Style")
        Assertions.assertThat(afterDelete.penaltyEntries).hasSize(1)
        Assertions.assertThat(afterDelete.penaltyEntries[0].name).isEqualTo("Penalty")
    }

    @Test
    fun `should add new entry to existing category`() {
        seedDefault()
        val existing = form()
        val dtId = existing.dtEntries.first().id
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = listOf(FormEntry(dtId, "DT", FormEntry.EntryType.PUNCTUATION, CalcType.AVERAGE)),
            style = listOf(
                FormEntry(styleId, "Style", FormEntry.EntryType.PUNCTUATION, CalcType.SUM),
                FormEntry(null, "Style 2", FormEntry.EntryType.PUNCTUATION, CalcType.SUM)
            ),
            penalty = listOf(FormEntry(penaltyId, "Penalty", FormEntry.EntryType.PUNCTUATION, CalcType.SUM))
        )

        val afterAdd = form()
        Assertions.assertThat(afterAdd.styleEntries).hasSize(2)
        Assertions.assertThat(afterAdd.styleEntries.map { it.name })
            .containsExactlyInAnyOrder("Style", "Style 2")

        Assertions.assertThat(afterAdd.dtEntries).hasSize(1)
        val dtEntry = afterAdd.dtEntries[0]
        Assertions.assertThat(dtEntry.name).isEqualTo("DT")
        Assertions.assertThat(dtEntry.calcType).isEqualTo(CalcType.AVERAGE)

        Assertions.assertThat(afterAdd.penaltyEntries).hasSize(1)
        Assertions.assertThat(afterAdd.penaltyEntries[0].name).isEqualTo("Penalty")
    }

    @Test
    fun `should set judges count for city`() {
        val city1 = cityClient.saveCity(CreateCityRequest("City 1"))
        val city2 = cityClient.saveCity(CreateCityRequest("City 2"))

        formClient.setJudgesCount(PROBLEM_ID, city1.id, 5)
        formClient.setJudgesCount(PROBLEM_ID, city2.id, 3)

        val city1Judges = formClient.getJudgeCount(PROBLEM_ID, city1.id)
        val city2Judges = formClient.getJudgeCount(PROBLEM_ID, city2.id)

        Assertions.assertThat(city1Judges).isEqualTo(5)
        Assertions.assertThat(city2Judges).isEqualTo(3)
    }

    @Test
    fun `should set form visual group`() {
        seedDefault()
        val existing = form()
        val dtId = existing.dtEntries.first().id
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = listOf(FormEntry(dtId, "DT", FormEntry.EntryType.PUNCTUATION, CalcType.AVERAGE)),
            style = listOf(
                FormEntry(styleId, "Style", FormEntry.EntryType.PUNCTUATION, CalcType.SUM),
                FormEntry(null, "Style 2", FormEntry.EntryType.PUNCTUATION, CalcType.SUM)
            ),
            penalty = listOf(FormEntry(penaltyId, "Penalty", FormEntry.EntryType.PUNCTUATION, CalcType.SUM))
        )
    }

    @Test
    fun `should support nested section entries`() {
        seedDefault()
        val existing = form()
        val dtId = existing.dtEntries.first().id

        setForm(
            dt = listOf(
                FormEntry(
                    dtId,
                    "DT Section",
                    FormEntry.EntryType.SECTION,
                    entries = listOf(
                        FormEntry(null, "Sub Entry 1", FormEntry.EntryType.PUNCTUATION, CalcType.SUM),
                        FormEntry(null, "Sub Entry 2", FormEntry.EntryType.PUNCTUATION, CalcType.AVERAGE)
                    )
                )
            ),
            style = existing.styleEntries,
            penalty = existing.penaltyEntries
        )

        val after = form()
        Assertions.assertThat(after.dtEntries).hasSize(1)
        val section = after.dtEntries[0]
        Assertions.assertThat(section.name).isEqualTo("DT Section")
        Assertions.assertThat(section.type).isEqualTo(FormEntry.EntryType.SECTION)
        Assertions.assertThat(section.entries).hasSize(2)
        Assertions.assertThat(section.entries.map { it.name })
            .containsExactlyInAnyOrder("Sub Entry 1", "Sub Entry 2")
    }

    @Test
    fun `should support punctuation group entries`() {
        seedDefault()

        setForm(
            dt = listOf(
                FormEntry(
                    null,
                    "DT Group",
                    FormEntry.EntryType.PUNCTUATION_GROUP,
                    entries = listOf(
                        FormEntry(null, "Group Entry 1", FormEntry.EntryType.PUNCTUATION, CalcType.SUM),
                        FormEntry(null, "Group Entry 2", FormEntry.EntryType.PUNCTUATION, CalcType.AVERAGE)
                    )
                )
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val after = form()
        Assertions.assertThat(after.dtEntries).hasSize(1)
        val group = after.dtEntries[0]
        Assertions.assertThat(group.name).isEqualTo("DT Group")
        Assertions.assertThat(group.type).isEqualTo(FormEntry.EntryType.PUNCTUATION_GROUP)
        Assertions.assertThat(group.entries).hasSize(2)
        Assertions.assertThat(group.entries.map { it.name })
            .containsExactlyInAnyOrder("Group Entry 1", "Group Entry 2")
    }
}