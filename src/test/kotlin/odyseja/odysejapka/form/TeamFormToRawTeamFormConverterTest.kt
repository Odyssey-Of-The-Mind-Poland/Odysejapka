package odyseja.odysejapka.form

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class TeamFormToRawTeamFormConverterTest {

    @Test
    fun `should convert basic team form with all entry types`() {
        val teamForm = TeamForm(
            performanceId = 1,
            teamName = "Team A",
            cityName = "City 1",
            problem = 1,
            age = 1,
            isFo = false,
            dtEntries = listOf(
                TeamForm.DtTeamFormEntry(
                    entry = LongTermFormEntry(
                        id = 1L,
                        name = "DT Entry 1",
                        type = LongTermFormEntry.EntryType.SCORING,
                        scoring = LongTermFormEntry.ScoringData(
                            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                            noElementEnabled = false
                        )
                    ),
                    results = mapOf(
                        JudgeType.DT_A to mapOf(1 to 10L, 2 to 20L),
                        JudgeType.DT_B to mapOf(1 to 15L)
                    )
                )
            ),
            styleEntries = listOf(
                TeamForm.StyleTeamFormEntry(
                    entry = StyleFormEntry(
                        id = 2L,
                        name = "Style Entry 1",
                        type = StyleFormEntry.EntryType.STYLE
                    ),
                    results = mapOf(
                        JudgeType.STYLE to mapOf(1 to 5L, 2 to 10L, 3 to 15L)
                    )
                )
            ),
            penaltyEntries = listOf(
                TeamForm.PenaltyTeamFormEntry(
                    entry = PenaltyFormEntry(
                        id = 3L,
                        name = "Penalty Entry 1",
                        type = PenaltyFormEntry.EntryType.PENALTY
                    ),
                    result = 2L
                )
            )
        )

        val result = TeamFormToRawTeamFormConverter.convert(teamForm)

        Assertions.assertThat(result.teamName).isEqualTo("Team A")
        Assertions.assertThat(result.cityName).isEqualTo("City 1")
        Assertions.assertThat(result.problem).isEqualTo(1)
        Assertions.assertThat(result.age).isEqualTo(1)

        Assertions.assertThat(result.dtEntries).hasSize(1)
        Assertions.assertThat(result.dtEntries[0].name).isEqualTo("DT Entry 1")
        Assertions.assertThat(result.dtEntries[0].averageScore).isEqualTo(15.0) // (10 + 20 + 15) / 3
        Assertions.assertThat(result.dtEntries[0].noElement).isFalse

        Assertions.assertThat(result.styleEntries).hasSize(1)
        Assertions.assertThat(result.styleEntries[0].name).isEqualTo("Style Entry 1")
        Assertions.assertThat(result.styleEntries[0].averageScore).isEqualTo(10.0) // (5 + 10 + 15) / 3

        Assertions.assertThat(result.penaltyEntries).hasSize(1)
        Assertions.assertThat(result.penaltyEntries[0].name).isEqualTo("Penalty Entry 1")
        Assertions.assertThat(result.penaltyEntries[0].averageScore).isEqualTo(2.0)

        Assertions.assertThat(result.dtSum).isEqualTo(15.0)
        Assertions.assertThat(result.styleSum).isEqualTo(10.0)
        Assertions.assertThat(result.penaltySum).isEqualTo(2.0)
        Assertions.assertThat(result.totalSum).isEqualTo(27.0)
    }

    @Test
    fun `should handle nested DT entries with indentation`() {
        val teamForm = TeamForm(
            performanceId = 1,
            teamName = "Team A",
            cityName = "City 1",
            problem = 1,
            age = 1,
            isFo = false,
            dtEntries = listOf(
                TeamForm.DtTeamFormEntry(
                    entry = LongTermFormEntry(
                        id = 1L,
                        name = "DT Section",
                        type = LongTermFormEntry.EntryType.SECTION
                    ),
                    results = emptyMap(),
                    nestedEntries = listOf(
                        TeamForm.DtTeamFormEntry(
                            entry = LongTermFormEntry(
                                id = 2L,
                                name = "Nested Entry 1",
                                type = LongTermFormEntry.EntryType.SCORING,
                                scoring = LongTermFormEntry.ScoringData(
                                    scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                                    noElementEnabled = false
                                )
                            ),
                            results = mapOf(JudgeType.DT_A to mapOf(1 to 10L))
                        ),
                        TeamForm.DtTeamFormEntry(
                            entry = LongTermFormEntry(
                                id = 3L,
                                name = "Nested Entry 2",
                                type = LongTermFormEntry.EntryType.SCORING,
                                scoring = LongTermFormEntry.ScoringData(
                                    scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                                    noElementEnabled = true
                                )
                            ),
                            results = mapOf(JudgeType.DT_A to mapOf(1 to 20L)),
                            noElement = true
                        )
                    )
                )
            ),
            styleEntries = emptyList(),
            penaltyEntries = emptyList()
        )

        val result = TeamFormToRawTeamFormConverter.convert(teamForm)

        Assertions.assertThat(result.dtEntries).hasSize(3)
        Assertions.assertThat(result.dtEntries[0].name).isEqualTo("DT Section")
        Assertions.assertThat(result.dtEntries[0].averageScore).isNull()
        Assertions.assertThat(result.dtEntries[1].name).isEqualTo("  Nested Entry 1")
        Assertions.assertThat(result.dtEntries[1].averageScore).isEqualTo(10.0)
        Assertions.assertThat(result.dtEntries[2].name).isEqualTo("  Nested Entry 2")
        Assertions.assertThat(result.dtEntries[2].averageScore).isEqualTo(20.0)
        Assertions.assertThat(result.dtEntries[2].noElement).isTrue
    }

    @Test
    fun `should handle empty results with null average`() {
        val teamForm = TeamForm(
            performanceId = 1,
            teamName = "Team A",
            cityName = "City 1",
            problem = 1,
            age = 1,
            isFo = false,
            dtEntries = listOf(
                TeamForm.DtTeamFormEntry(
                    entry = LongTermFormEntry(
                        id = 1L,
                        name = "DT Entry",
                        type = LongTermFormEntry.EntryType.SCORING,
                        scoring = LongTermFormEntry.ScoringData(
                            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                            noElementEnabled = false
                        )
                    ),
                    results = emptyMap()
                )
            ),
            styleEntries = listOf(
                TeamForm.StyleTeamFormEntry(
                    entry = StyleFormEntry(
                        id = 2L,
                        name = "Style Entry",
                        type = StyleFormEntry.EntryType.STYLE
                    ),
                    results = emptyMap()
                )
            ),
            penaltyEntries = listOf(
                TeamForm.PenaltyTeamFormEntry(
                    entry = PenaltyFormEntry(
                        id = 3L,
                        name = "Penalty Entry",
                        type = PenaltyFormEntry.EntryType.PENALTY
                    ),
                    result = null
                )
            )
        )

        val result = TeamFormToRawTeamFormConverter.convert(teamForm)

        Assertions.assertThat(result.dtEntries[0].averageScore).isNull()
        Assertions.assertThat(result.styleEntries[0].averageScore).isNull()
        Assertions.assertThat(result.penaltyEntries[0].averageScore).isNull()

        Assertions.assertThat(result.dtSum).isEqualTo(0.0)
        Assertions.assertThat(result.styleSum).isEqualTo(0.0)
        Assertions.assertThat(result.penaltySum).isEqualTo(0.0)
        Assertions.assertThat(result.totalSum).isEqualTo(0.0)
    }

    @Test
    fun `should calculate average correctly with multiple judges and scores`() {
        val teamForm = TeamForm(
            performanceId = 1,
            teamName = "Team A",
            cityName = "City 1",
            problem = 1,
            age = 1,
            isFo = false,
            dtEntries = listOf(
                TeamForm.DtTeamFormEntry(
                    entry = LongTermFormEntry(
                        id = 1L,
                        name = "DT Entry",
                        type = LongTermFormEntry.EntryType.SCORING,
                        scoring = LongTermFormEntry.ScoringData(
                            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                            noElementEnabled = false
                        )
                    ),
                    results = mapOf(
                        JudgeType.DT_A to mapOf(1 to 10L, 2 to 20L, 3 to 30L),
                        JudgeType.DT_B to mapOf(1 to 15L, 2 to 25L)
                    )
                )
            ),
            styleEntries = emptyList(),
            penaltyEntries = emptyList()
        )

        val result = TeamFormToRawTeamFormConverter.convert(teamForm)

        // Average of: 10, 20, 30, 15, 25 = 100 / 5 = 20.0
        Assertions.assertThat(result.dtEntries[0].averageScore).isEqualTo(20.0)
    }

    @Test
    fun `should filter out null scores when calculating average`() {
        val teamForm = TeamForm(
            performanceId = 1,
            teamName = "Team A",
            cityName = "City 1",
            problem = 1,
            age = 1,
            isFo = false,
            dtEntries = listOf(
                TeamForm.DtTeamFormEntry(
                    entry = LongTermFormEntry(
                        id = 1L,
                        name = "DT Entry",
                        type = LongTermFormEntry.EntryType.SCORING,
                        scoring = LongTermFormEntry.ScoringData(
                            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                            noElementEnabled = false
                        )
                    ),
                    results = mapOf(
                        JudgeType.DT_A to mapOf(1 to 10L, 2 to null, 3 to 30L),
                        JudgeType.DT_B to mapOf(1 to null, 2 to 25L)
                    )
                )
            ),
            styleEntries = emptyList(),
            penaltyEntries = emptyList()
        )

        val result = TeamFormToRawTeamFormConverter.convert(teamForm)

        // Average of: 10, 30, 25 = 65 / 3 = 21.666...
        Assertions.assertThat(result.dtEntries[0].averageScore).isEqualTo(21.666666666666668)
    }

    @Test
    fun `should handle multiple entries in each category`() {
        val teamForm = TeamForm(
            performanceId = 1,
            teamName = "Team A",
            cityName = "City 1",
            problem = 1,
            age = 1,
            isFo = false,
            dtEntries = listOf(
                TeamForm.DtTeamFormEntry(
                    entry = LongTermFormEntry(
                        id = 1L,
                        name = "DT Entry 1",
                        type = LongTermFormEntry.EntryType.SCORING,
                        scoring = LongTermFormEntry.ScoringData(
                            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                            noElementEnabled = false
                        )
                    ),
                    results = mapOf(JudgeType.DT_A to mapOf(1 to 10L))
                ),
                TeamForm.DtTeamFormEntry(
                    entry = LongTermFormEntry(
                        id = 2L,
                        name = "DT Entry 2",
                        type = LongTermFormEntry.EntryType.SCORING,
                        scoring = LongTermFormEntry.ScoringData(
                            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                            noElementEnabled = false
                        )
                    ),
                    results = mapOf(JudgeType.DT_A to mapOf(1 to 20L))
                )
            ),
            styleEntries = listOf(
                TeamForm.StyleTeamFormEntry(
                    entry = StyleFormEntry(
                        id = 3L,
                        name = "Style Entry 1",
                        type = StyleFormEntry.EntryType.STYLE
                    ),
                    results = mapOf(JudgeType.STYLE to mapOf(1 to 5L))
                ),
                TeamForm.StyleTeamFormEntry(
                    entry = StyleFormEntry(
                        id = 4L,
                        name = "Style Entry 2",
                        type = StyleFormEntry.EntryType.STYLE
                    ),
                    results = mapOf(JudgeType.STYLE to mapOf(1 to 15L))
                )
            ),
            penaltyEntries = listOf(
                TeamForm.PenaltyTeamFormEntry(
                    entry = PenaltyFormEntry(
                        id = 5L,
                        name = "Penalty Entry 1",
                        type = PenaltyFormEntry.EntryType.PENALTY
                    ),
                    result = 1L
                ),
                TeamForm.PenaltyTeamFormEntry(
                    entry = PenaltyFormEntry(
                        id = 6L,
                        name = "Penalty Entry 2",
                        type = PenaltyFormEntry.EntryType.PENALTY
                    ),
                    result = 3L
                )
            )
        )

        val result = TeamFormToRawTeamFormConverter.convert(teamForm)

        Assertions.assertThat(result.dtEntries).hasSize(2)
        Assertions.assertThat(result.styleEntries).hasSize(2)
        Assertions.assertThat(result.penaltyEntries).hasSize(2)

        Assertions.assertThat(result.dtSum).isEqualTo(30.0) // 10 + 20
        Assertions.assertThat(result.styleSum).isEqualTo(20.0) // 5 + 15
        Assertions.assertThat(result.penaltySum).isEqualTo(4.0) // 1 + 3
        Assertions.assertThat(result.totalSum).isEqualTo(54.0) // 30 + 20 + 4
    }

    @Test
    fun `should handle deeply nested DT entries`() {
        val teamForm = TeamForm(
            performanceId = 1,
            teamName = "Team A",
            cityName = "City 1",
            problem = 1,
            age = 1,
            isFo = false,
            dtEntries = listOf(
                TeamForm.DtTeamFormEntry(
                    entry = LongTermFormEntry(
                        id = 1L,
                        name = "Level 1",
                        type = LongTermFormEntry.EntryType.SECTION
                    ),
                    results = emptyMap(),
                    nestedEntries = listOf(
                        TeamForm.DtTeamFormEntry(
                            entry = LongTermFormEntry(
                                id = 2L,
                                name = "Level 2",
                                type = LongTermFormEntry.EntryType.SECTION
                            ),
                            results = emptyMap(),
                            nestedEntries = listOf(
                                TeamForm.DtTeamFormEntry(
                                    entry = LongTermFormEntry(
                                        id = 3L,
                                        name = "Level 3",
                                        type = LongTermFormEntry.EntryType.SCORING,
                                        scoring = LongTermFormEntry.ScoringData(
                                            scoringType = LongTermFormEntry.ScoringType.SUBJECTIVE,
                                            noElementEnabled = false
                                        )
                                    ),
                                    results = mapOf(JudgeType.DT_A to mapOf(1 to 10L))
                                )
                            )
                        )
                    )
                )
            ),
            styleEntries = emptyList(),
            penaltyEntries = emptyList()
        )

        val result = TeamFormToRawTeamFormConverter.convert(teamForm)

        Assertions.assertThat(result.dtEntries).hasSize(3)
        Assertions.assertThat(result.dtEntries[0].name).isEqualTo("Level 1")
        Assertions.assertThat(result.dtEntries[1].name).isEqualTo("  Level 2")
        Assertions.assertThat(result.dtEntries[2].name).isEqualTo("    Level 3")
        Assertions.assertThat(result.dtEntries[2].averageScore).isEqualTo(10.0)
    }
}

