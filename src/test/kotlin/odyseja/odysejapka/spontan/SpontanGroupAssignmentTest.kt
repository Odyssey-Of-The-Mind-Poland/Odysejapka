package odyseja.odysejapka.spontan

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMINISTRATOR"])
class SpontanGroupAssignmentTest : OdysejaDsl() {

    @Test
    fun `should list groups for city with no assignments`() {
        val city = createCity("SpontanCity1")
        createPerformance(city.id, problem = 1, age = 1, league = "")

        val groups = spontanClient.getGroupAssignments(city.id)

        assertThat(groups).hasSize(1)
        assertThat(groups[0].groupId).isEqualTo(GroupId(1, 1, ""))
        assertThat(groups[0].spontanDefinitionId).isNull()
        assertThat(groups[0].judgeCount).isEqualTo(3)
    }

    @Test
    fun `should assign spontan to group`() {
        val city = createCity("SpontanCity2")
        createPerformance(city.id, problem = 1, age = 1, league = "")

        val spontan = spontanClient.create(
            SpontanDefinition(name = "Test Spontan", type = SpontanType.VERBAL)
        )

        val result = spontanClient.assignSpontan(
            city.id, 1, 1, "",
            AssignSpontanRequest(spontan.id)
        )

        assertThat(result.spontanDefinitionId).isEqualTo(spontan.id)
        assertThat(result.spontanDefinitionName).isEqualTo("Test Spontan")
        assertThat(result.spontanType).isEqualTo(SpontanType.VERBAL)
    }

    @Test
    fun `should unassign spontan from group`() {
        val city = createCity("SpontanCity3")
        createPerformance(city.id, problem = 1, age = 1, league = "")

        val spontan = spontanClient.create(
            SpontanDefinition(name = "To Unassign", type = SpontanType.VERBAL)
        )

        spontanClient.assignSpontan(city.id, 1, 1, "", AssignSpontanRequest(spontan.id))
        val result = spontanClient.assignSpontan(city.id, 1, 1, "", AssignSpontanRequest(null))

        assertThat(result.spontanDefinitionId).isNull()
        assertThat(result.spontanDefinitionName).isNull()
    }

    @Test
    fun `should set judge count`() {
        val city = createCity("SpontanCity4")
        createPerformance(city.id, problem = 1, age = 1, league = "")

        val result = spontanClient.setJudgeCount(city.id, 1, 1, "", SetJudgeCountRequest(4))

        assertThat(result.judgeCount).isEqualTo(4)
    }

    @Test
    fun `should persist assignment and show in group list`() {
        val city = createCity("SpontanCity5")
        createPerformance(city.id, problem = 1, age = 1, league = "")

        val spontan = spontanClient.create(
            SpontanDefinition(name = "Persistent", type = SpontanType.MANUAL, multiplier = 2.0)
        )

        spontanClient.assignSpontan(city.id, 1, 1, "", AssignSpontanRequest(spontan.id))
        spontanClient.setJudgeCount(city.id, 1, 1, "", SetJudgeCountRequest(2))

        val groups = spontanClient.getGroupAssignments(city.id)

        assertThat(groups).hasSize(1)
        assertThat(groups[0].spontanDefinitionId).isEqualTo(spontan.id)
        assertThat(groups[0].spontanDefinitionName).isEqualTo("Persistent")
        assertThat(groups[0].spontanType).isEqualTo(SpontanType.MANUAL)
        assertThat(groups[0].judgeCount).isEqualTo(2)
    }
}
