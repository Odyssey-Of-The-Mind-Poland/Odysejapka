package odyseja.odysejapka.form

import odyseja.odysejapka.OdysejaDsl
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

@WithMockUser(username = "testuser", roles = ["ADMINISTRATOR"])
class JudgeTeamTest : OdysejaDsl() {

    @Test
    fun `should create judge team`() {
        val city = createCity("Sędziszów")
        val desiredJudgeTeam = JudgeTeam(PROBLEM_ID, city.id,
            JudgeCount(2, 2, 2)
        )
        setJudgeTeams(listOf(desiredJudgeTeam))

        val actualJudgeCount = formClient.getJudgeCount(PROBLEM_ID, city.id)

        Assertions.assertThat(actualJudgeCount.dtA).isEqualTo(2)
        Assertions.assertThat(actualJudgeCount.dtB).isEqualTo(2)
        Assertions.assertThat(actualJudgeCount.style).isEqualTo(2)

        val actualJudgeTeam = getJudgeTeams(PROBLEM_ID)
        Assertions.assertThat(actualJudgeTeam).contains(desiredJudgeTeam)
    }

    @Test
    fun `should have a judge count of 1-1-1 by default`() {
        val city = createCity("Sędziowie nie są zdefiniowani")

        Assertions.assertThatNoException().isThrownBy {
            formClient.getJudgeCount(PROBLEM_ID, city.id)
        }
        val judgeCount = formClient.getJudgeCount(PROBLEM_ID, city.id)
        Assertions.assertThat(judgeCount.dtA).isEqualTo(1)
        Assertions.assertThat(judgeCount.dtB).isEqualTo(1)
        Assertions.assertThat(judgeCount.style).isEqualTo(1)
    }

    @Test
    fun `should update judge count`() {
        clearCities()
        val city = createCity("Jedyne miasto")
        val oldJudgeTeam = JudgeTeam(PROBLEM_ID, city.id,
            JudgeCount(2, 2, 2)
        )
        setJudgeTeams(listOf(oldJudgeTeam))
        Assertions.assertThat(formClient.getJudgeCount(PROBLEM_ID, city.id)).isEqualTo(oldJudgeTeam.judgeCount)

        val newJudgeTeam = JudgeTeam(PROBLEM_ID, city.id,
            JudgeCount(1, 1, 1)
        )
        setJudgeTeams(listOf(newJudgeTeam))
        Assertions.assertThat(formClient.getJudgeCount(PROBLEM_ID, city.id)).isEqualTo(newJudgeTeam.judgeCount)

        val actualJudgeTeams = getJudgeTeams(PROBLEM_ID)
        Assertions.assertThat(actualJudgeTeams).containsExactly(newJudgeTeam)
    }

    @Test
    fun `should remove judge team on city deletion`() {
        val city = createCity("Jedyne miasto")
        val judgeTeam = JudgeTeam(PROBLEM_ID, city.id,
            JudgeCount(2, 2, 2)
        )
        setJudgeTeams(listOf(judgeTeam))
        clearCities()

        val judgeTeams = getJudgeTeams(PROBLEM_ID)
        Assertions.assertThat(judgeTeams).isEmpty()
    }

    @Test
    fun `can accept unequal numbers of judges`() {
        val city = createCity("212")
        val judgeTeam = JudgeTeam(PROBLEM_ID, city.id,
            JudgeCount(2, 1, 2)
        )
        setJudgeTeams(listOf(judgeTeam))

        val actualJudgeTeam = formClient.getJudgeCount(PROBLEM_ID, city.id)

        Assertions.assertThat(actualJudgeTeam.dtA).isEqualTo(2)
        Assertions.assertThat(actualJudgeTeam.dtB).isEqualTo(1)
        Assertions.assertThat(actualJudgeTeam.style).isEqualTo(2)
    }

    @Test
    fun `creates different teams for different problems`() {
        val cityA = createCity("Ananasowo")
        val cityB = createCity("Bakłażanów")

        val judgeTeamA = JudgeTeam(1, cityA.id, JudgeCount(2, 2, 2))
        val judgeTeamB = JudgeTeam(1, cityB.id, JudgeCount(1, 1, 1))
        val judgeTeamC = JudgeTeam(2, cityA.id, JudgeCount(2, 1, 2))
        val judgeTeamD = JudgeTeam(2, cityB.id, JudgeCount(1, 2, 1))

        setJudgeTeams(listOf(judgeTeamA, judgeTeamB), 1)
        setJudgeTeams(listOf(judgeTeamC, judgeTeamD), 2)

        val problem1Teams = getJudgeTeams(1)
        val problem2Teams = getJudgeTeams(2)

        Assertions.assertThat(problem1Teams).contains(judgeTeamA, judgeTeamB)
        Assertions.assertThat(problem2Teams).contains(judgeTeamC, judgeTeamD)

        Assertions.assertThat(formClient.getJudgeCount(1, cityA.id)).isEqualTo(JudgeCount(2, 2, 2))
        Assertions.assertThat(formClient.getJudgeCount(1, cityB.id)).isEqualTo(JudgeCount(1, 1, 1))
        Assertions.assertThat(formClient.getJudgeCount(2, cityA.id)).isEqualTo(JudgeCount(2, 1, 2))
        Assertions.assertThat(formClient.getJudgeCount(2, cityB.id)).isEqualTo(JudgeCount(1, 2, 1))

    }
}