package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormSortIndexTest : OdysejaDsl() {

    @Test
    fun `should assign sequential sort indexes to DT entries`() {
        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        setForm(
            dt = listOf(
                LongTermFormEntry(null, "Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                LongTermFormEntry(null, "Entry 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1),
                LongTermFormEntry(null, "Entry 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 2)
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val entries = form()
        Assertions.assertThat(entries.dtEntries).hasSize(3)
        Assertions.assertThat(entries.dtEntries[0].sortIndex).isEqualTo(0)
        Assertions.assertThat(entries.dtEntries[0].name).isEqualTo("Entry 1")
        Assertions.assertThat(entries.dtEntries[1].sortIndex).isEqualTo(1)
        Assertions.assertThat(entries.dtEntries[1].name).isEqualTo("Entry 2")
        Assertions.assertThat(entries.dtEntries[2].sortIndex).isEqualTo(2)
        Assertions.assertThat(entries.dtEntries[2].name).isEqualTo("Entry 3")
    }

    @Test
    fun `should update sort indexes when entries are reordered`() {
        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        // Create initial entries
        setForm(
            dt = listOf(
                LongTermFormEntry(null, "Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                LongTermFormEntry(null, "Entry 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1),
                LongTermFormEntry(null, "Entry 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 2)
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val initial = form()
        val entry1Id = initial.dtEntries[0].id!!
        val entry2Id = initial.dtEntries[1].id!!
        val entry3Id = initial.dtEntries[2].id!!

        // Reorder entries (swap first and last)
        setForm(
            dt = listOf(
                LongTermFormEntry(entry3Id, "Entry 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                LongTermFormEntry(entry2Id, "Entry 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1),
                LongTermFormEntry(entry1Id, "Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 2)
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val reordered = form()
        Assertions.assertThat(reordered.dtEntries).hasSize(3)
        Assertions.assertThat(reordered.dtEntries[0].sortIndex).isEqualTo(0)
        Assertions.assertThat(reordered.dtEntries[0].name).isEqualTo("Entry 3")
        Assertions.assertThat(reordered.dtEntries[1].sortIndex).isEqualTo(1)
        Assertions.assertThat(reordered.dtEntries[1].name).isEqualTo("Entry 2")
        Assertions.assertThat(reordered.dtEntries[2].sortIndex).isEqualTo(2)
        Assertions.assertThat(reordered.dtEntries[2].name).isEqualTo("Entry 1")
    }

    @Test
    fun `should assign sequential sort indexes to style entries`() {
        setForm(
            dt = emptyList(),
            style = listOf(
                StyleFormEntry(null, "Style 1", StyleFormEntry.EntryType.STYLE, sortIndex = 0),
                StyleFormEntry(null, "Style 2", StyleFormEntry.EntryType.STYLE, sortIndex = 1),
                StyleFormEntry(null, "Style 3", StyleFormEntry.EntryType.STYLE, sortIndex = 2)
            ),
            penalty = emptyList()
        )

        val entries = form()
        Assertions.assertThat(entries.styleEntries).hasSize(3)
        Assertions.assertThat(entries.styleEntries[0].sortIndex).isEqualTo(0)
        Assertions.assertThat(entries.styleEntries[0].name).isEqualTo("Style 1")
        Assertions.assertThat(entries.styleEntries[1].sortIndex).isEqualTo(1)
        Assertions.assertThat(entries.styleEntries[1].name).isEqualTo("Style 2")
        Assertions.assertThat(entries.styleEntries[2].sortIndex).isEqualTo(2)
        Assertions.assertThat(entries.styleEntries[2].name).isEqualTo("Style 3")
    }

    @Test
    fun `should assign sequential sort indexes to penalty entries`() {
        setForm(
            dt = emptyList(),
            style = emptyList(),
            penalty = listOf(
                PenaltyFormEntry(null, "Penalty 1", PenaltyFormEntry.EntryType.PENALTY, sortIndex = 0),
                PenaltyFormEntry(null, "Penalty 2", PenaltyFormEntry.EntryType.PENALTY, sortIndex = 1),
                PenaltyFormEntry(null, "Penalty 3", PenaltyFormEntry.EntryType.PENALTY, sortIndex = 2)
            )
        )

        val entries = form()
        Assertions.assertThat(entries.penaltyEntries).hasSize(3)
        Assertions.assertThat(entries.penaltyEntries[0].sortIndex).isEqualTo(0)
        Assertions.assertThat(entries.penaltyEntries[0].name).isEqualTo("Penalty 1")
        Assertions.assertThat(entries.penaltyEntries[1].sortIndex).isEqualTo(1)
        Assertions.assertThat(entries.penaltyEntries[1].name).isEqualTo("Penalty 2")
        Assertions.assertThat(entries.penaltyEntries[2].sortIndex).isEqualTo(2)
        Assertions.assertThat(entries.penaltyEntries[2].name).isEqualTo("Penalty 3")
    }

    @Test
    fun `should recalculate sort indexes when entry is removed`() {
        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        // Create initial entries
        setForm(
            dt = listOf(
                LongTermFormEntry(null, "Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                LongTermFormEntry(null, "Entry 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1),
                LongTermFormEntry(null, "Entry 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 2)
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val initial = form()
        val entry1Id = initial.dtEntries[0].id!!
        val entry3Id = initial.dtEntries[2].id!!

        // Remove middle entry
        setForm(
            dt = listOf(
                LongTermFormEntry(entry1Id, "Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                LongTermFormEntry(entry3Id, "Entry 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1)
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val afterRemove = form()
        Assertions.assertThat(afterRemove.dtEntries).hasSize(2)
        Assertions.assertThat(afterRemove.dtEntries[0].sortIndex).isEqualTo(0)
        Assertions.assertThat(afterRemove.dtEntries[0].name).isEqualTo("Entry 1")
        Assertions.assertThat(afterRemove.dtEntries[1].sortIndex).isEqualTo(1)
        Assertions.assertThat(afterRemove.dtEntries[1].name).isEqualTo("Entry 3")
    }

    @Test
    fun `should assign sequential sort indexes to nested entries`() {
        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        setForm(
            dt = listOf(
                LongTermFormEntry(
                    null,
                    "Section",
                    LongTermFormEntry.EntryType.SECTION,
                    entries = listOf(
                        LongTermFormEntry(null, "Nested 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                        LongTermFormEntry(null, "Nested 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1),
                        LongTermFormEntry(null, "Nested 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 2)
                    ),
                    sortIndex = 0
                )
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val entries = form()
        Assertions.assertThat(entries.dtEntries).hasSize(1)
        val section = entries.dtEntries[0]
        Assertions.assertThat(section.entries).hasSize(3)
        Assertions.assertThat(section.entries[0].sortIndex).isEqualTo(0)
        Assertions.assertThat(section.entries[0].name).isEqualTo("Nested 1")
        Assertions.assertThat(section.entries[1].sortIndex).isEqualTo(1)
        Assertions.assertThat(section.entries[1].name).isEqualTo("Nested 2")
        Assertions.assertThat(section.entries[2].sortIndex).isEqualTo(2)
        Assertions.assertThat(section.entries[2].name).isEqualTo("Nested 3")
    }

    @Test
    fun `should update sort indexes when nested entries are reordered`() {
        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        // Create initial nested entries
        setForm(
            dt = listOf(
                LongTermFormEntry(
                    null,
                    "Section",
                    LongTermFormEntry.EntryType.SECTION,
                    entries = listOf(
                        LongTermFormEntry(null, "Nested 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                        LongTermFormEntry(null, "Nested 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1),
                        LongTermFormEntry(null, "Nested 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 2)
                    ),
                    sortIndex = 0
                )
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val initial = form()
        val sectionId = initial.dtEntries[0].id!!
        val nested1Id = initial.dtEntries[0].entries[0].id!!
        val nested2Id = initial.dtEntries[0].entries[1].id!!
        val nested3Id = initial.dtEntries[0].entries[2].id!!

        // Reorder nested entries (swap first and last)
        setForm(
            dt = listOf(
                LongTermFormEntry(
                    sectionId,
                    "Section",
                    LongTermFormEntry.EntryType.SECTION,
                    entries = listOf(
                        LongTermFormEntry(nested3Id, "Nested 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                        LongTermFormEntry(nested2Id, "Nested 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1),
                        LongTermFormEntry(nested1Id, "Nested 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 2)
                    ),
                    sortIndex = 0
                )
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val reordered = form()
        val section = reordered.dtEntries[0]
        Assertions.assertThat(section.entries).hasSize(3)
        Assertions.assertThat(section.entries[0].sortIndex).isEqualTo(0)
        Assertions.assertThat(section.entries[0].name).isEqualTo("Nested 3")
        Assertions.assertThat(section.entries[1].sortIndex).isEqualTo(1)
        Assertions.assertThat(section.entries[1].name).isEqualTo("Nested 2")
        Assertions.assertThat(section.entries[2].sortIndex).isEqualTo(2)
        Assertions.assertThat(section.entries[2].name).isEqualTo("Nested 1")
    }

    @Test
    fun `should maintain sort indexes when entry is added in the middle`() {
        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        // Create initial entries
        setForm(
            dt = listOf(
                LongTermFormEntry(null, "Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                LongTermFormEntry(null, "Entry 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1)
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val initial = form()
        val entry1Id = initial.dtEntries[0].id!!
        val entry3Id = initial.dtEntries[1].id!!

        // Add entry in the middle
        setForm(
            dt = listOf(
                LongTermFormEntry(entry1Id, "Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 0),
                LongTermFormEntry(null, "Entry 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 1),
                LongTermFormEntry(entry3Id, "Entry 3", LongTermFormEntry.EntryType.SCORING, scoring = scoringData, sortIndex = 2)
            ),
            style = emptyList(),
            penalty = emptyList()
        )

        val afterAdd = form()
        Assertions.assertThat(afterAdd.dtEntries).hasSize(3)
        Assertions.assertThat(afterAdd.dtEntries[0].sortIndex).isEqualTo(0)
        Assertions.assertThat(afterAdd.dtEntries[0].name).isEqualTo("Entry 1")
        Assertions.assertThat(afterAdd.dtEntries[1].sortIndex).isEqualTo(1)
        Assertions.assertThat(afterAdd.dtEntries[1].name).isEqualTo("Entry 2")
        Assertions.assertThat(afterAdd.dtEntries[2].sortIndex).isEqualTo(2)
        Assertions.assertThat(afterAdd.dtEntries[2].name).isEqualTo("Entry 3")
    }
}

