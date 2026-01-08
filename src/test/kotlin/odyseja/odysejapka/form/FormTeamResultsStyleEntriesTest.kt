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
                    pointsMin = 0,
                    pointsMax = 100,
                    judges = LongTermFormEntry.JudgeType.A,
                    noElement = false
                )
            )),
            style = listOf(
                LongTermFormEntry(
                    null, "Style Entry 1", LongTermFormEntry.EntryType.STYLE
                ),
                LongTermFormEntry(
                    null, "Style Entry 2", LongTermFormEntry.EntryType.STYLE
                )
            ),
            penalty = listOf(LongTermFormEntry(
                null, "Penalty", LongTermFormEntry.EntryType.SCORING,
                scoring = LongTermFormEntry.ScoringData(
                    scoringType = LongTermFormEntry.ScoringType.OBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 10,
                    judges = LongTermFormEntry.JudgeType.A,
                    noElement = false
                )
            ))
        )

        val entries = form()
        Assertions.assertThat(entries.styleEntries).hasSize(2)
        Assertions.assertThat(entries.styleEntries[0].type).isEqualTo(LongTermFormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[0].name).isEqualTo("Style Entry 1")
        Assertions.assertThat(entries.styleEntries[0].scoring).isNull()
        Assertions.assertThat(entries.styleEntries[0].scoringGroup).isNull()
        Assertions.assertThat(entries.styleEntries[1].type).isEqualTo(LongTermFormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[1].name).isEqualTo("Style Entry 2")
        Assertions.assertThat(entries.styleEntries[1].scoring).isNull()
        Assertions.assertThat(entries.styleEntries[1].scoringGroup).isNull()
    }

    @Test
    fun `should include STYLE entries in team results`() {
        setForm(
            dt = listOf(LongTermFormEntry(
                null, "DT", LongTermFormEntry.EntryType.SCORING,
                scoring = LongTermFormEntry.ScoringData(
                    scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 100,
                    judges = LongTermFormEntry.JudgeType.A,
                    noElement = false
                )
            )),
            style = listOf(
                LongTermFormEntry(
                    null, "Style Only", LongTermFormEntry.EntryType.STYLE
                )
            ),
            penalty = listOf(LongTermFormEntry(
                null, "Penalty", LongTermFormEntry.EntryType.SCORING,
                scoring = LongTermFormEntry.ScoringData(
                    scoringType = LongTermFormEntry.ScoringType.OBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 10,
                    judges = LongTermFormEntry.JudgeType.A,
                    noElement = false
                )
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
        Assertions.assertThat(saved.entries).hasSize(3) // dt, style, penalty
        val styleEntry = saved.entries.first { it.entryId == styleId }
        Assertions.assertThat(styleEntry.judgeResults).isEmpty() // STYLE entries don't accept results
    }

    @Test
    fun `should support mixed SCORING and STYLE entries in style category`() {
        setForm(
            dt = listOf(LongTermFormEntry(
                null, "DT", LongTermFormEntry.EntryType.SCORING,
                scoring = LongTermFormEntry.ScoringData(
                    scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 100,
                    judges = LongTermFormEntry.JudgeType.A,
                    noElement = false
                )
            )),
            style = listOf(
                LongTermFormEntry(
                    null, "Scoring Style", LongTermFormEntry.EntryType.SCORING,
                    scoring = LongTermFormEntry.ScoringData(
                        scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                        pointsMin = 0,
                        pointsMax = 50,
                        judges = LongTermFormEntry.JudgeType.B,
                        noElement = false
                    )
                ),
                LongTermFormEntry(
                    null, "Style Only", LongTermFormEntry.EntryType.STYLE
                )
            ),
            penalty = emptyList()
        )

        val entries = form()
        Assertions.assertThat(entries.styleEntries).hasSize(2)
        Assertions.assertThat(entries.styleEntries[0].type).isEqualTo(LongTermFormEntry.EntryType.SCORING)
        Assertions.assertThat(entries.styleEntries[0].scoring).isNotNull()
        Assertions.assertThat(entries.styleEntries[1].type).isEqualTo(LongTermFormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[1].scoring).isNull()

        val scoringStyleId = entries.styleEntries[0].id!!
        val styleOnlyId = entries.styleEntries[1].id!!

        val city = createCity("Wrocław")
        val perfId = createPerformance(city.id)

        setTeamResults(perfId, listOf(performanceResult(scoringStyleId, 40)))

        val saved = getTeamResults(perfId)
        Assertions.assertThat(saved.entries).hasSize(3) // dt, 2 style entries, 0 penalty
        val scoringStyleEntry = saved.entries.first { it.entryId == scoringStyleId }
        val styleOnlyEntry = saved.entries.first { it.entryId == styleOnlyId }
        Assertions.assertThat(scoringStyleEntry.judgeResults).containsEntry(1, 40L)
        Assertions.assertThat(styleOnlyEntry.judgeResults).isEmpty()
    }
}

