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
        Assertions.assertThat(dtEntry.type).isEqualTo(LongTermFormEntry.EntryType.SCORING)
        Assertions.assertThat(dtEntry.scoring).isNotNull
        Assertions.assertThat(dtEntry.scoring?.scoringType).isEqualTo(LongTermFormEntry.ScoringType.SUBJECTIVE)
        val styleEntry = entries.styleEntries[0]
        Assertions.assertThat(styleEntry.name).isEqualTo("Style")
        Assertions.assertThat(styleEntry.type).isEqualTo(StyleFormEntry.EntryType.STYLE)
    }

    @Test
    fun `should update entry`() {
        seedDefault()
        val existing = form()

        val dtId = existing.dtEntries.first().id
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = listOf(LongTermFormEntry(
                dtId, "DT new", LongTermFormEntry.EntryType.SCORING,
                scoring = LongTermFormEntry.ScoringData(
                    scoringType = LongTermFormEntry.ScoringType.OBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 200,
                    judges = LongTermFormEntry.JudgeType.B,
                    noElementEnabled = true
                )
            )),
            style = listOf(StyleFormEntry(
                styleId, "Style", StyleFormEntry.EntryType.STYLE
            )),
            penalty = listOf(PenaltyFormEntry(
                penaltyId, "Penalty", PenaltyFormEntry.EntryType.PENALTY
            ))
        )

        val updated = form()
        Assertions.assertThat(updated.dtEntries).hasSize(1)
        val updatedDt = updated.dtEntries[0]
        Assertions.assertThat(updatedDt.name).isEqualTo("DT new")
        Assertions.assertThat(updatedDt.scoring?.pointsMax).isEqualTo(200)
        Assertions.assertThat(updatedDt.scoring?.noElementEnabled).isTrue
    }

    @Test
    fun `should delete entry when omitted from request`() {
        seedDefault()
        val existing = form()
        val styleId = existing.styleEntries.first().id
        val penaltyId = existing.penaltyEntries.first().id

        setForm(
            dt = emptyList(),
            style = listOf(StyleFormEntry(
                styleId, "Style", StyleFormEntry.EntryType.STYLE
            )),
            penalty = listOf(PenaltyFormEntry(
                penaltyId, "Penalty", PenaltyFormEntry.EntryType.PENALTY
            ))
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

        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            pointsMin = 0,
            pointsMax = 100,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        setForm(
            dt = listOf(LongTermFormEntry(dtId, "DT", LongTermFormEntry.EntryType.SCORING, scoring = scoringData)),
            style = listOf(
                StyleFormEntry(styleId, "Style", StyleFormEntry.EntryType.STYLE),
                StyleFormEntry(null, "Style 2", StyleFormEntry.EntryType.STYLE)
            ),
            penalty = listOf(PenaltyFormEntry(penaltyId, "Penalty", PenaltyFormEntry.EntryType.PENALTY))
        )

        val afterAdd = form()
        Assertions.assertThat(afterAdd.styleEntries).hasSize(2)
        Assertions.assertThat(afterAdd.styleEntries.map { it.name })
            .containsExactlyInAnyOrder("Style", "Style 2")

        Assertions.assertThat(afterAdd.dtEntries).hasSize(1)
        val dtEntry = afterAdd.dtEntries[0]
        Assertions.assertThat(dtEntry.name).isEqualTo("DT")

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

        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            pointsMin = 0,
            pointsMax = 100,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        setForm(
            dt = listOf(LongTermFormEntry(dtId, "DT", LongTermFormEntry.EntryType.SCORING, scoring = scoringData)),
            style = listOf(
                StyleFormEntry(styleId, "Style", StyleFormEntry.EntryType.STYLE),
                StyleFormEntry(null, "Style 2", StyleFormEntry.EntryType.STYLE)
            ),
            penalty = listOf(PenaltyFormEntry(penaltyId, "Penalty", PenaltyFormEntry.EntryType.PENALTY))
        )
    }

    @Test
    fun `should support nested section entries`() {
        seedDefault()
        val existing = form()
        val dtId = existing.dtEntries.first().id

        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            pointsMin = 0,
            pointsMax = 100,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        setForm(
            dt = listOf(
                LongTermFormEntry(
                    dtId,
                    "DT Section",
                    LongTermFormEntry.EntryType.SECTION,
                    entries = listOf(
                        LongTermFormEntry(null, "Sub Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData),
                        LongTermFormEntry(null, "Sub Entry 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData)
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
        Assertions.assertThat(section.type).isEqualTo(LongTermFormEntry.EntryType.SECTION)
        Assertions.assertThat(section.entries).hasSize(2)
        Assertions.assertThat(section.entries.map { it.name })
            .containsExactlyInAnyOrder("Sub Entry 1", "Sub Entry 2")
    }

    @Test
    fun `should support scoring group entries`() {
        seedDefault()

        val scoringData = LongTermFormEntry.ScoringData(
            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
            pointsMin = 0,
            pointsMax = 100,
            judges = LongTermFormEntry.JudgeType.A,
            noElementEnabled = false
        )

        setForm(
            dt = listOf(
                LongTermFormEntry(
                    null,
                    "DT Group",
                    LongTermFormEntry.EntryType.SCORING_GROUP,
                    scoringGroup = LongTermFormEntry.ScoringGroupData(
                        pointsMin = 0,
                        pointsMax = 200
                    ),
                    entries = listOf(
                        LongTermFormEntry(null, "Group Entry 1", LongTermFormEntry.EntryType.SCORING, scoring = scoringData),
                        LongTermFormEntry(null, "Group Entry 2", LongTermFormEntry.EntryType.SCORING, scoring = scoringData)
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
        Assertions.assertThat(group.type).isEqualTo(LongTermFormEntry.EntryType.SCORING_GROUP)
        Assertions.assertThat(group.scoringGroup).isNotNull
        Assertions.assertThat(group.scoringGroup?.pointsMin).isEqualTo(0)
        Assertions.assertThat(group.scoringGroup?.pointsMax).isEqualTo(200)
        Assertions.assertThat(group.entries).hasSize(2)
        Assertions.assertThat(group.entries.map { it.name })
            .containsExactlyInAnyOrder("Group Entry 1", "Group Entry 2")
    }
}