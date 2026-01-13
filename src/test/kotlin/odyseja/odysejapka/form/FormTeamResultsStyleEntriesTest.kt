package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormTeamResultsStyleEntriesTest : OdysejaDsl() {

    @Test
    fun `should handle STYLE entries in form`() {
        setForm(
            dt = listOf(LongTermFormEntry(
                null, "DT", LongTermFormEntry.EntryType.SCORING,
                scoring = LongTermFormEntry.ScoringData(
                    scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                    noElementEnabled = false
                )
            )),
            style = listOf(
                StyleFormEntry(
                    null, "Style Entry 1", StyleFormEntry.EntryType.STYLE
                ),
                StyleFormEntry(
                    null, "Style Entry 2", StyleFormEntry.EntryType.STYLE
                )
            ),
            penalty = listOf(PenaltyFormEntry(
                null, "Penalty", PenaltyFormEntry.EntryType.PENALTY
            ))
        )

        val entries = form()
        Assertions.assertThat(entries.styleEntries).hasSize(2)
        Assertions.assertThat(entries.styleEntries[0].type).isEqualTo(StyleFormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[0].name).isEqualTo("Style Entry 1")
        Assertions.assertThat(entries.styleEntries[1].type).isEqualTo(StyleFormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[1].name).isEqualTo("Style Entry 2")
    }

    @Test
    fun `should include STYLE entries in team results`() {
        setForm(
            dt = listOf(LongTermFormEntry(
                null, "DT", LongTermFormEntry.EntryType.SCORING,
                scoring = LongTermFormEntry.ScoringData(
                    scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                    noElementEnabled = false
                )
            )),
            style = listOf(
                StyleFormEntry(
                    null, "Style Only", StyleFormEntry.EntryType.STYLE
                )
            ),
            penalty = listOf(PenaltyFormEntry(
                null, "Penalty", PenaltyFormEntry.EntryType.PENALTY
            ))
        )

        val entries = form()
        val dtId = entries.dtEntries.first().id!!
        val styleId = entries.styleEntries.first().id!!
        val penaltyId = entries.penaltyEntries.first().id!!

        val city = createCity("Poznań")
        val perfId = createPerformance(city.id)

        setTeamResults(perfId, listOf(
            performanceResult(dtId, 75),
            performanceResult(penaltyId, 5)
        ))

        val saved = getTeamResults(perfId)
        val allEntries = saved.dtEntries + saved.styleEntries + saved.penaltyEntries
        Assertions.assertThat(allEntries).hasSize(3) // dt, style, penalty
        val styleEntry = saved.styleEntries.first { it.entry.id == styleId }
        Assertions.assertThat(styleEntry.judgeResults).isEmpty() // STYLE entries don't accept results
    }

    @Test
    fun `should support multiple STYLE entries in style category`() {
        setForm(
            dt = listOf(LongTermFormEntry(
                null, "DT", LongTermFormEntry.EntryType.SCORING,
                scoring = LongTermFormEntry.ScoringData(
                    scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                    noElementEnabled = false
                )
            )),
            style = listOf(
                StyleFormEntry(
                    null, "Style Entry 1", StyleFormEntry.EntryType.STYLE
                ),
                StyleFormEntry(
                    null, "Style Entry 2", StyleFormEntry.EntryType.STYLE
                )
            ),
            penalty = emptyList()
        )

        val entries = form()
        Assertions.assertThat(entries.styleEntries).hasSize(2)
        Assertions.assertThat(entries.styleEntries[0].type).isEqualTo(StyleFormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[0].name).isEqualTo("Style Entry 1")
        Assertions.assertThat(entries.styleEntries[1].type).isEqualTo(StyleFormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[1].name).isEqualTo("Style Entry 2")

        val style1Id = entries.styleEntries[0].id!!
        val style2Id = entries.styleEntries[1].id!!

        val city = createCity("Wrocław")
        val perfId = createPerformance(city.id)

        val saved = getTeamResults(perfId)
        val allEntries = saved.dtEntries + saved.styleEntries + saved.penaltyEntries
        Assertions.assertThat(allEntries).hasSize(3) // dt, 2 style entries, 0 penalty
        val style1Entry = saved.styleEntries.first { it.entry.id == style1Id }
        val style2Entry = saved.styleEntries.first { it.entry.id == style2Id }
        Assertions.assertThat(style1Entry.judgeResults).isEmpty() // STYLE entries don't accept results
        Assertions.assertThat(style2Entry.judgeResults).isEmpty()
    }
}

