package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormTeamResultsBasicTest : OdysejaDsl() {

    @Test
    fun `should add new team results and fetch them`() {
        val (dtId, styleId, _) = seedDefaultFormWithIds()
        val city = createCity("Rzeszów")
        val perfId = createPerformance(city.id)

        setTeamResults(perfId, listOf(
            performanceResult(dtId, 50),
            performanceResult(styleId, 30)
        ))

        val saved = getTeamResults(perfId)
        val allEntries = saved.dtEntries + saved.styleEntries + saved.penaltyEntries
        Assertions.assertThat(allEntries).hasSize(3) // All form entries (dt, style, penalty)
        val dtEntry = saved.dtEntries.first { it.entry.id == dtId }
        val styleEntry = saved.styleEntries.first { it.entry.id == styleId }
        Assertions.assertThat(dtEntry.judgeResults).containsEntry(1, 50L)
        Assertions.assertThat(styleEntry.judgeResults).containsEntry(1, 30L)
    }

    @Test
    fun `should update existing result and add another judge`() {
        val (dtId, styleId, _) = seedDefaultFormWithIds()
        val city = createCity("Gdynia")
        val perfId = createPerformance(city.id)

        setTeamResults(perfId, listOf(
            performanceResult(dtId, 10),
            performanceResult(styleId, 20)
        ))

        setTeamResults(perfId, listOf(
            performanceResult(dtId, 15),
            performanceResult(styleId, 22, judge = 2)
        ))

        val after = getTeamResults(perfId)
        val allEntries = after.dtEntries + after.styleEntries + after.penaltyEntries

        Assertions.assertThat(allEntries).hasSize(3) // All form entries (dt, style, penalty)
        val dtEntry = after.dtEntries.first { it.entry.id == dtId }
        val styleEntry = after.styleEntries.first { it.entry.id == styleId }
        Assertions.assertThat(dtEntry.judgeResults).containsEntry(1, 15L)
        Assertions.assertThat(styleEntry.judgeResults).containsEntry(1, 20L)
        Assertions.assertThat(styleEntry.judgeResults).containsEntry(2, 22L)
    }

    @Test
    fun `should be idempotent when sending unchanged values`() {
        val (dtId, styleId, _) = seedDefaultFormWithIds()
        val city = createCity("Białystok")
        val perfId = createPerformance(city.id)

        val payload = listOf(
            performanceResult(dtId, 44),
            performanceResult(styleId, 66)
        )

        setTeamResults(perfId, payload)
        val first = getTeamResults(perfId)
        val firstAll = first.dtEntries + first.styleEntries + first.penaltyEntries
        Assertions.assertThat(firstAll).hasSize(3) // All form entries (dt, style, penalty)

        setTeamResults(perfId, payload)
        val second = getTeamResults(perfId)
        val secondAll = second.dtEntries + second.styleEntries + second.penaltyEntries
        Assertions.assertThat(secondAll).hasSize(3)
        val dtEntry = second.dtEntries.first { it.entry.id == dtId }
        val styleEntry = second.styleEntries.first { it.entry.id == styleId }
        Assertions.assertThat(dtEntry.judgeResults).containsEntry(1, 44L)
        Assertions.assertThat(styleEntry.judgeResults).containsEntry(1, 66L)
    }

    @Test
    fun `empty payload is a no-op`() {
        seedDefaultFormWithIds()
        val city = createCity("Słupsk")
        val perfId = createPerformance(city.id)

        setTeamResults(perfId, emptyList())

        val after = getTeamResults(perfId)
        val allEntries = after.dtEntries + after.styleEntries + after.penaltyEntries
        Assertions.assertThat(allEntries).hasSize(3) // All form entries (dt, style, penalty) but with empty judgeResults
        Assertions.assertThat(allEntries.all { it.judgeResults.isEmpty() }).isTrue()
    }
}

