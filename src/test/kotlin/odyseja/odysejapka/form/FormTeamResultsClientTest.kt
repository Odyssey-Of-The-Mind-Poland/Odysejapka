package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.city.CreateCityRequest
import odyseja.odysejapka.timetable.Performance
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser
import java.time.LocalDate

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormTeamResultsClientTest : OdysejaDsl() {

    private fun seedDefaultForm(): Triple<Long, Long, Long> {
        setForm(
            dt = listOf(FormEntry(
                null, "DT", FormEntry.EntryType.SCORING,
                scoring = FormEntry.ScoringData(
                    scoringType = FormEntry.ScoringType.SUBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 100,
                    judges = FormEntry.JudgeType.A,
                    noElement = false
                )
            )),
            style = listOf(FormEntry(
                null, "Style", FormEntry.EntryType.SCORING,
                scoring = FormEntry.ScoringData(
                    scoringType = FormEntry.ScoringType.SUBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 50,
                    judges = FormEntry.JudgeType.B,
                    noElement = false
                )
            )),
            penalty = listOf(FormEntry(
                null, "Penalty", FormEntry.EntryType.SCORING,
                scoring = FormEntry.ScoringData(
                    scoringType = FormEntry.ScoringType.OBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 10,
                    judges = FormEntry.JudgeType.A,
                    noElement = false
                )
            ))
        )
        val entries = form()
        val dtId = entries.dtEntries.first().id!!
        val styleId = entries.styleEntries.first().id!!
        val penaltyId = entries.penaltyEntries.first().id!!
        return Triple(dtId, styleId, penaltyId)
    }

    private fun createCity(name: String) = cityClient.saveCity(CreateCityRequest(name))

    private fun createPerformance(cityId: Int): Int {
        val cityName = cityClient.getCities().firstOrNull { it?.id == cityId }?.name ?: "Unknown"
        val performance = Performance(
            city = cityName, team = "Sample Team",
            id = 0,
            problem = 1,
            age = 1,
            stage = 1,
            performance = "",
            spontan = "",
            part = 1,
            performanceDay = "",
            spontanDay = "",
            league = "",
            zspRow = 1,
            zspSheet = "",
            performanceDate = LocalDate.now(),
        )
        return timeTableClient.addPerformance(performance).id
    }

    private fun results(performanceId: Int) = formClient.getTeamResults(performanceId)


    @Test
    fun `should add new team results and fetch them`() {
        val (dtId, styleId, _) = seedDefaultForm()
        val city = createCity("Rzeszów")
        val perfId = createPerformance(city.id)

        formClient.setTeamResults(
            perfId,
            PerformanceResultsRequest(
                listOf(
                    PerformanceResultsRequest.PerformanceResult(entryId = dtId, result = 50, judge = 1),
                    PerformanceResultsRequest.PerformanceResult(entryId = styleId, result = 30, judge = 1)
                )
            )
        )

        val saved = results(perfId)
        Assertions.assertThat(saved.entries).hasSize(3) // All form entries (dt, style, penalty)
        val dtEntry = saved.entries.first { it.entryId == dtId }
        val styleEntry = saved.entries.first { it.entryId == styleId }
        Assertions.assertThat(dtEntry.judgeResults).containsEntry(1, 50L)
        Assertions.assertThat(styleEntry.judgeResults).containsEntry(1, 30L)
    }

    @Test
    fun `should update existing result and add another judge`() {
        val (dtId, styleId, _) = seedDefaultForm()
        val city = createCity("Gdynia")
        val perfId = createPerformance(city.id)

        formClient.setTeamResults(
            perfId,
            PerformanceResultsRequest(
                listOf(
                    PerformanceResultsRequest.PerformanceResult(dtId, 10, judge = 1),
                    PerformanceResultsRequest.PerformanceResult(styleId, 20, judge = 1)
                )
            )
        )

        formClient.setTeamResults(
            perfId,
            PerformanceResultsRequest(
                listOf(
                    PerformanceResultsRequest.PerformanceResult(dtId, 15, judge = 1),
                    PerformanceResultsRequest.PerformanceResult(styleId, 22, judge = 2)
                )
            )
        )

        val after = results(perfId).entries

        Assertions.assertThat(after).hasSize(3) // All form entries (dt, style, penalty)
        val dtEntry = after.first { it.entryId == dtId }
        val styleEntry = after.first { it.entryId == styleId }
        Assertions.assertThat(dtEntry.judgeResults).containsEntry(1, 15L)
        Assertions.assertThat(styleEntry.judgeResults).containsEntry(1, 20L)
        Assertions.assertThat(styleEntry.judgeResults).containsEntry(2, 22L)
    }

    @Test
    fun `should be idempotent when sending unchanged values`() {
        val (dtId, styleId, _) = seedDefaultForm()
        val city = createCity("Białystok")
        val perfId = createPerformance(city.id)

        val payload = PerformanceResultsRequest(
            listOf(
                PerformanceResultsRequest.PerformanceResult(dtId, 44, judge = 1),
                PerformanceResultsRequest.PerformanceResult(styleId, 66, judge = 1)
            )
        )

        formClient.setTeamResults(perfId, payload)
        val first = results(perfId).entries
        Assertions.assertThat(first).hasSize(3) // All form entries (dt, style, penalty)

        formClient.setTeamResults(perfId, payload)
        val second = results(perfId).entries
        Assertions.assertThat(second).hasSize(3)
        val dtEntry = second.first { it.entryId == dtId }
        val styleEntry = second.first { it.entryId == styleId }
        Assertions.assertThat(dtEntry.judgeResults).containsEntry(1, 44L)
        Assertions.assertThat(styleEntry.judgeResults).containsEntry(1, 66L)
    }

    @Test
    fun `should keep results isolated between cities and support different judge counts`() {
        val (dtId, _, _) = seedDefaultForm()
        val cityA = createCity("Warszawa")
        val cityB = createCity("Kraków")

        formClient.setJudgesCount(PROBLEM_ID, cityA.id, 3)
        formClient.setJudgesCount(PROBLEM_ID, cityB.id, 5)

        val perfA = createPerformance(cityA.id)
        val perfB = createPerformance(cityB.id)

        // zapisz wyniki dla różnych sędziów i miast
        formClient.setTeamResults(
            perfA,
            PerformanceResultsRequest(listOf(PerformanceResultsRequest.PerformanceResult(dtId, 11, judge = 1)))
        )
        formClient.setTeamResults(
            perfB,
            PerformanceResultsRequest(
                listOf(
                    PerformanceResultsRequest.PerformanceResult(dtId, 22, judge = 1),
                    PerformanceResultsRequest.PerformanceResult(dtId, 33, judge = 2),
                )
            )
        )

        val a = results(perfA).entries
        val b = results(perfB).entries

        Assertions.assertThat(a).hasSize(3) // All form entries (dt, style, penalty)
        val dtEntryA = a.first { it.entryId == dtId }
        Assertions.assertThat(dtEntryA.judgeResults).containsEntry(1, 11L)

        Assertions.assertThat(b).hasSize(3) // All form entries (dt, style, penalty)
        val dtEntryB = b.first { it.entryId == dtId }
        Assertions.assertThat(dtEntryB.judgeResults.keys).containsExactlyInAnyOrder(1, 2)
        Assertions.assertThat(dtEntryB.judgeResults).containsEntry(1, 22L)
        Assertions.assertThat(dtEntryB.judgeResults).containsEntry(2, 33L)

        Assertions.assertThat(formClient.getJudgeCount(PROBLEM_ID, cityA.id)).isEqualTo(3)
        Assertions.assertThat(formClient.getJudgeCount(PROBLEM_ID, cityB.id)).isEqualTo(5)
    }

    @Test
    fun `should throw for unknown form entry id`() {
        seedDefaultForm()
        val city = createCity("Opole")
        val perfId = createPerformance(city.id)

        val unknownId = 9_999_999L

        Assertions.assertThatThrownBy {
            formClient.setTeamResults(
                perfId,
                PerformanceResultsRequest(
                    listOf(PerformanceResultsRequest.PerformanceResult(unknownId, 1, judge = 1))
                )
            )
        }
            .hasMessageContaining("Unknown form entry id")
    }

    @Test
    fun `empty payload is a no-op`() {
        seedDefaultForm()
        val city = createCity("Słupsk")
        val perfId = createPerformance(city.id)

        formClient.setTeamResults(perfId, PerformanceResultsRequest(emptyList()))

        val after = results(perfId).entries
        Assertions.assertThat(after).hasSize(3) // All form entries (dt, style, penalty) but with empty judgeResults
        Assertions.assertThat(after.all { it.judgeResults.isEmpty() }).isTrue()
    }

    @Test
    fun `should handle STYLE entries in form`() {
        setForm(
            dt = listOf(FormEntry(
                null, "DT", FormEntry.EntryType.SCORING,
                scoring = FormEntry.ScoringData(
                    scoringType = FormEntry.ScoringType.SUBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 100,
                    judges = FormEntry.JudgeType.A,
                    noElement = false
                )
            )),
            style = listOf(
                FormEntry(
                    null, "Style Entry 1", FormEntry.EntryType.STYLE
                ),
                FormEntry(
                    null, "Style Entry 2", FormEntry.EntryType.STYLE
                )
            ),
            penalty = listOf(FormEntry(
                null, "Penalty", FormEntry.EntryType.SCORING,
                scoring = FormEntry.ScoringData(
                    scoringType = FormEntry.ScoringType.OBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 10,
                    judges = FormEntry.JudgeType.A,
                    noElement = false
                )
            ))
        )

        val entries = form()
        Assertions.assertThat(entries.styleEntries).hasSize(2)
        Assertions.assertThat(entries.styleEntries[0].type).isEqualTo(FormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[0].name).isEqualTo("Style Entry 1")
        Assertions.assertThat(entries.styleEntries[0].scoring).isNull()
        Assertions.assertThat(entries.styleEntries[0].scoringGroup).isNull()
        Assertions.assertThat(entries.styleEntries[1].type).isEqualTo(FormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[1].name).isEqualTo("Style Entry 2")
        Assertions.assertThat(entries.styleEntries[1].scoring).isNull()
        Assertions.assertThat(entries.styleEntries[1].scoringGroup).isNull()
    }

    @Test
    fun `should include STYLE entries in team results`() {
        setForm(
            dt = listOf(FormEntry(
                null, "DT", FormEntry.EntryType.SCORING,
                scoring = FormEntry.ScoringData(
                    scoringType = FormEntry.ScoringType.SUBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 100,
                    judges = FormEntry.JudgeType.A,
                    noElement = false
                )
            )),
            style = listOf(
                FormEntry(
                    null, "Style Only", FormEntry.EntryType.STYLE
                )
            ),
            penalty = listOf(FormEntry(
                null, "Penalty", FormEntry.EntryType.SCORING,
                scoring = FormEntry.ScoringData(
                    scoringType = FormEntry.ScoringType.OBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 10,
                    judges = FormEntry.JudgeType.A,
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

        formClient.setTeamResults(
            perfId,
            PerformanceResultsRequest(
                listOf(
                    PerformanceResultsRequest.PerformanceResult(entryId = dtId, result = 75, judge = 1),
                    PerformanceResultsRequest.PerformanceResult(entryId = penaltyId, result = 5, judge = 1)
                )
            )
        )

        val saved = results(perfId)
        Assertions.assertThat(saved.entries).hasSize(3) // dt, style, penalty
        val styleEntry = saved.entries.first { it.entryId == styleId }
        Assertions.assertThat(styleEntry.judgeResults).isEmpty() // STYLE entries don't accept results
    }

    @Test
    fun `should support mixed SCORING and STYLE entries in style category`() {
        setForm(
            dt = listOf(FormEntry(
                null, "DT", FormEntry.EntryType.SCORING,
                scoring = FormEntry.ScoringData(
                    scoringType = FormEntry.ScoringType.SUBJECTIVE,
                    pointsMin = 0,
                    pointsMax = 100,
                    judges = FormEntry.JudgeType.A,
                    noElement = false
                )
            )),
            style = listOf(
                FormEntry(
                    null, "Scoring Style", FormEntry.EntryType.SCORING,
                    scoring = FormEntry.ScoringData(
                        scoringType = FormEntry.ScoringType.SUBJECTIVE,
                        pointsMin = 0,
                        pointsMax = 50,
                        judges = FormEntry.JudgeType.B,
                        noElement = false
                    )
                ),
                FormEntry(
                    null, "Style Only", FormEntry.EntryType.STYLE
                )
            ),
            penalty = emptyList()
        )

        val entries = form()
        Assertions.assertThat(entries.styleEntries).hasSize(2)
        Assertions.assertThat(entries.styleEntries[0].type).isEqualTo(FormEntry.EntryType.SCORING)
        Assertions.assertThat(entries.styleEntries[0].scoring).isNotNull()
        Assertions.assertThat(entries.styleEntries[1].type).isEqualTo(FormEntry.EntryType.STYLE)
        Assertions.assertThat(entries.styleEntries[1].scoring).isNull()

        val scoringStyleId = entries.styleEntries[0].id!!
        val styleOnlyId = entries.styleEntries[1].id!!

        val city = createCity("Wrocław")
        val perfId = createPerformance(city.id)

        formClient.setTeamResults(
            perfId,
            PerformanceResultsRequest(
                listOf(
                    PerformanceResultsRequest.PerformanceResult(entryId = scoringStyleId, result = 40, judge = 1)
                )
            )
        )

        val saved = results(perfId)
        Assertions.assertThat(saved.entries).hasSize(3) // dt, 2 style entries, 0 penalty
        val scoringStyleEntry = saved.entries.first { it.entryId == scoringStyleId }
        val styleOnlyEntry = saved.entries.first { it.entryId == styleOnlyId }
        Assertions.assertThat(scoringStyleEntry.judgeResults).containsEntry(1, 40L)
        Assertions.assertThat(styleOnlyEntry.judgeResults).isEmpty()
    }
}