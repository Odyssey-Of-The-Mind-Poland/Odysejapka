package odyseja.odysejapka.rak

import odyseja.odysejapka.OdysejaDsl
import odyseja.odysejapka.city.CreateCityRequest
import odyseja.odysejapka.form.FormEntry
import odyseja.odysejapka.form.ProblemForm
import odyseja.odysejapka.form.PerformanceResultsRequest
import odyseja.odysejapka.form.FormResult
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormTeamResultsClientTest : OdysejaDsl() {

    private fun seedDefaultForm(): Triple<Long, Long, Long> {
        setForm(
            dt = listOf(FormEntry(null, "DT", FormEntry.CalcType.AVERAGE)),
            style = listOf(FormEntry(null, "Style", FormEntry.CalcType.SUM)),
            penalty = listOf(FormEntry(null, "Penalty", FormEntry.CalcType.SUM))
        )
        val entries = form()
        val dtId = entries.dtEntries.first().id!!
        val styleId = entries.styleEntries.first().id!!
        val penaltyId = entries.penaltyEntries.first().id!!
        return Triple(dtId, styleId, penaltyId)
    }

    private fun createCity(name: String) = cityClient.saveCity(CreateCityRequest(name))

    private fun createPerformance(cityId: Int): Int {
        return timetableClient.createSimplePerformance(PROBLEM_ID, cityId).id
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
        assertThat(saved).hasSize(2)
        assertThat(saved.map { it.entryId to it.result to it.judge })
            .containsExactlyInAnyOrder(
                (dtId to 50L) to 1,
                (styleId to 30L) to 1
            )
        assertThat(saved.map { it.performanceId }.toSet()).containsExactly(perfId)
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

        val after = results(perfId)

        assertThat(after).hasSize(3)
        assertThat(after.first { it.entryId == dtId && it.judge == 1 }.result).isEqualTo(15L)
        assertThat(after.first { it.entryId == styleId && it.judge == 1 }.result).isEqualTo(20L)
        assertThat(after.first { it.entryId == styleId && it.judge == 2 }.result).isEqualTo(22L)
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
        val first = results(perfId)
        assertThat(first).hasSize(2)

        formClient.setTeamResults(perfId, payload)
        val second = results(perfId)
        assertThat(second).hasSize(2)
        assertThat(second.first { it.entryId == dtId && it.judge == 1 }.result).isEqualTo(44L)
        assertThat(second.first { it.entryId == styleId && it.judge == 1 }.result).isEqualTo(66L)
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

        val a = results(perfA)
        val b = results(perfB)

        assertThat(a).hasSize(1)
        assertThat(a[0].performanceId).isEqualTo(perfA)
        assertThat(a[0].judge).isEqualTo(1)
        assertThat(a[0].result).isEqualTo(11L)

        assertThat(b).hasSize(2)
        assertThat(b.map { it.performanceId }.toSet()).containsExactly(perfB)
        assertThat(b.map { it.judge }).containsExactlyInAnyOrder(1, 2)

        assertThat(formClient.getJudgeCount(PROBLEM_ID, cityA.id)).isEqualTo(3)
        assertThat(formClient.getJudgeCount(PROBLEM_ID, cityB.id)).isEqualTo(5)
    }

    @Test
    fun `should throw for unknown form entry id`() {
        seedDefaultForm()
        val city = createCity("Opole")
        val perfId = createPerformance(city.id)

        val unknownId = 9_999_999L

        assertThatThrownBy {
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

        val after = results(perfId)
        assertThat(after).isEmpty()
    }
}
