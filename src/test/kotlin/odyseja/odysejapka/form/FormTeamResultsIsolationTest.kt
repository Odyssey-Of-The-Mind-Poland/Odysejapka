package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMIN"])
class FormTeamResultsIsolationTest : OdysejaDsl() {

    @Test
    fun `should keep results isolated between cities and support different judge counts`() {
        val (dtId, _, _) = seedDefaultFormWithIds()
        val cityA = createCity("Warszawa")
        val cityB = createCity("Kraków")

        val existingForm = form()
        formClient.setProblemForm(
            PROBLEM_ID,
            existingForm.copy(
                smallJudgesTeam = listOf(cityA.id),
                bigJudgesTeam = listOf(cityB.id)
            )
        )

        val perfA = createPerformance(cityA.id)
        val perfB = createPerformance(cityB.id)

        setTeamResults(perfA, listOf(performanceResult(dtId, 11)))
        setTeamResults(
            perfB, listOf(
                performanceResult(dtId, 22),
                performanceResult(dtId, 33, judge = 2)
            )
        )

        val a = getTeamResults(perfA)
        val b = getTeamResults(perfB)
        val aAll = a.dtEntries.size + a.styleEntries.size + a.penaltyEntries.size
        val bAll = b.dtEntries.size + b.styleEntries.size + b.penaltyEntries.size

        Assertions.assertThat(aAll).isEqualTo(3) // All form entries (dt, style, penalty)
        val dtEntryA = a.dtEntries.first { it.entry.id == dtId }
        Assertions.assertThat(dtEntryA.judgesA).containsEntry(1, 11L)

        Assertions.assertThat(bAll).isEqualTo(3) // All form entries (dt, style, penalty)
        val dtEntryB = b.dtEntries.first { it.entry.id == dtId }
        Assertions.assertThat(dtEntryB.judgesA.keys).containsExactlyInAnyOrder(1, 2)
        Assertions.assertThat(dtEntryB.judgesA).containsEntry(1, 22L)
        Assertions.assertThat(dtEntryB.judgesA).containsEntry(2, 33L)
    }
}

