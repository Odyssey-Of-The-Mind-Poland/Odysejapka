package odyseja.odysejapka.spontan

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMINISTRATOR"])
class SpontanResultTest : OdysejaDsl() {

    @Test
    fun `should get group teams sorted by spontan hour`() {
        val city = createCity("ResultCity1")
        createPerformance(city.id, team = "Team A", spontan = "10:00", spontanDay = "sobota")
        createPerformance(city.id, team = "Team B", spontan = "09:00", spontanDay = "sobota")

        val spontan = spontanClient.create(
            SpontanDefinition(name = "Verbal Test", type = SpontanType.VERBAL)
        )
        spontanClient.assignSpontan(city.id, 1, 1, "", AssignSpontanRequest(spontan.id))

        val result = spontanClient.getGroupTeams(city.id, 1, 1, "")

        assertThat(result.spontanDefinition.name).isEqualTo("Verbal Test")
        assertThat(result.judgeCount).isEqualTo(3)
        assertThat(result.teams).hasSize(2)
        assertThat(result.teams[0].team).isEqualTo("Team B") // 09:00 before 10:00
        assertThat(result.teams[1].team).isEqualTo("Team A")
    }

    @Test
    fun `should save and return verbal spontan results`() {
        val city = createCity("ResultCity2")
        val perfId = createPerformance(city.id, team = "Team V", spontan = "10:00", spontanDay = "sobota")

        val spontan = spontanClient.create(
            SpontanDefinition(name = "Verbal Scoring", type = SpontanType.VERBAL)
        )
        spontanClient.assignSpontan(city.id, 1, 1, "", AssignSpontanRequest(spontan.id))

        val result = spontanClient.setResults(perfId, SpontanResultsRequest(
            entries = listOf(
                SpontanResultEntry(judge = 1, field = "creative", value = 3.0),
                SpontanResultEntry(judge = 1, field = "common", value = 5.0),
                SpontanResultEntry(judge = 2, field = "creative", value = 4.0),
                SpontanResultEntry(judge = 2, field = "common", value = 2.0),
                SpontanResultEntry(judge = 3, field = "creative", value = 2.0),
                SpontanResultEntry(judge = 3, field = "common", value = 3.0)
            )
        ))

        assertThat(result.entries).hasSize(6)
        // Judge 1: 3*5 + 5 = 20
        // Judge 2: 4*5 + 2 = 22
        // Judge 3: 2*5 + 3 = 13
        // Average: (20 + 22 + 13) / 3 = 18.333...
        assertThat(result.rawSpontan).isCloseTo(18.333, org.assertj.core.data.Offset.offset(0.01))
    }

    @Test
    fun `should save and return manual spontan results`() {
        val city = createCity("ResultCity3")
        val perfId = createPerformance(city.id, team = "Team M", spontan = "10:00", spontanDay = "sobota")

        val spontan = spontanClient.create(
            SpontanDefinition(
                name = "Manual Scoring",
                type = SpontanType.MANUAL,
                multiplier = 2.0,
                fields = listOf(
                    SpontanFieldEntry(name = "Element1", multiplier = 1.5),
                    SpontanFieldEntry(name = "Element2", multiplier = 2.0)
                )
            )
        )
        spontanClient.assignSpontan(city.id, 1, 1, "", AssignSpontanRequest(spontan.id))
        spontanClient.setJudgeCount(city.id, 1, 1, "", SetJudgeCountRequest(2))

        val result = spontanClient.setResults(perfId, SpontanResultsRequest(
            entries = listOf(
                SpontanResultEntry(judge = 1, field = "creativity", value = 8.0),
                SpontanResultEntry(judge = 1, field = "teamwork", value = 7.0),
                SpontanResultEntry(judge = 2, field = "creativity", value = 6.0),
                SpontanResultEntry(judge = 2, field = "teamwork", value = 9.0),
                SpontanResultEntry(judge = 0, field = "Element1", value = 3.0),
                SpontanResultEntry(judge = 0, field = "Element2", value = 4.0)
            )
        ))

        // Judge score: (8+7 + 6+9) / 2 = 30/2 = 15
        // Field score: 3*1.5 + 4*2.0 = 4.5 + 8 = 12.5
        // Total: 15 + 12.5 = 27.5
        assertThat(result.rawSpontan).isCloseTo(27.5, org.assertj.core.data.Offset.offset(0.01))
    }

    @Test
    fun `should persist results and show in team list`() {
        val city = createCity("ResultCity4")
        val perfId = createPerformance(city.id, team = "Team P", spontan = "11:00", spontanDay = "sobota")

        val spontan = spontanClient.create(
            SpontanDefinition(name = "Persist Test", type = SpontanType.VERBAL)
        )
        spontanClient.assignSpontan(city.id, 1, 1, "", AssignSpontanRequest(spontan.id))

        spontanClient.setResults(perfId, SpontanResultsRequest(
            entries = listOf(
                SpontanResultEntry(judge = 1, field = "creative", value = 5.0),
                SpontanResultEntry(judge = 1, field = "common", value = 3.0)
            )
        ))

        val groupTeams = spontanClient.getGroupTeams(city.id, 1, 1, "")
        val team = groupTeams.teams.find { it.performanceId == perfId }

        assertThat(team).isNotNull
        assertThat(team!!.entries).hasSize(2)
        assertThat(team.rawSpontan).isNotNull()
    }

    @Test
    fun `should update results when saving again`() {
        val city = createCity("ResultCity5")
        val perfId = createPerformance(city.id, team = "Team U", spontan = "12:00", spontanDay = "sobota")

        val spontan = spontanClient.create(
            SpontanDefinition(name = "Update Test", type = SpontanType.VERBAL)
        )
        spontanClient.assignSpontan(city.id, 1, 1, "", AssignSpontanRequest(spontan.id))

        spontanClient.setResults(perfId, SpontanResultsRequest(
            entries = listOf(
                SpontanResultEntry(judge = 1, field = "creative", value = 1.0),
                SpontanResultEntry(judge = 1, field = "common", value = 1.0)
            )
        ))

        val updated = spontanClient.setResults(perfId, SpontanResultsRequest(
            entries = listOf(
                SpontanResultEntry(judge = 1, field = "creative", value = 10.0),
                SpontanResultEntry(judge = 1, field = "common", value = 5.0)
            )
        ))

        // Judge 1: 10*5 + 5 = 55 / 3 judges = 18.333
        assertThat(updated.rawSpontan).isCloseTo(18.333, org.assertj.core.data.Offset.offset(0.01))
    }
}
