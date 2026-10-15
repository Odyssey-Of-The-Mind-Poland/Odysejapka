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

        val actualJudgeTeam = formClient.getJudgeCountV2(PROBLEM_ID, city.id)

        Assertions.assertThat(actualJudgeTeam).isInstanceOf(JudgeCount::class.java)
        Assertions.assertThat(actualJudgeTeam.dtA).isEqualTo(2)
        Assertions.assertThat(actualJudgeTeam.dtB).isEqualTo(2)
        Assertions.assertThat(actualJudgeTeam.style).isEqualTo(2)

        val actualForm = formClient.getFormDataV2(PROBLEM_ID)
        Assertions.assertThat(actualForm.judgeTeams).contains(desiredJudgeTeam)
    }

    @Test
    fun `should have a judge count of 1-1-1 by default`() {
        val city = createCity("Sędziowie nie są zdefiniowani")

        Assertions.assertThatNoException().isThrownBy {
            formClient.getJudgeCountV2(PROBLEM_ID, city.id)
        }
        val judgeCount = formClient.getJudgeCountV2(PROBLEM_ID, city.id)
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
        Assertions.assertThat(formClient.getJudgeCountV2(PROBLEM_ID, city.id)).isEqualTo(oldJudgeTeam.judgeCount)

        val newJudgeTeam = JudgeTeam(PROBLEM_ID, city.id,
            JudgeCount(1, 1, 1)
        )
        setJudgeTeams(listOf(newJudgeTeam))
        Assertions.assertThat(formClient.getJudgeCountV2(PROBLEM_ID, city.id)).isEqualTo(newJudgeTeam.judgeCount)

        val actualForm = formClient.getFormDataV2(PROBLEM_ID)
        Assertions.assertThat(actualForm.judgeTeams).containsExactly(newJudgeTeam)
    }

    @Test
    fun `should remove judge team on city deletion`() {
        val city = createCity("Jedyne miasto")
        val judgeTeam = JudgeTeam(PROBLEM_ID, city.id,
            JudgeCount(2, 2, 2)
        )
        setJudgeTeams(listOf(judgeTeam))
        clearCities()

        val form = formClient.getFormDataV2(PROBLEM_ID)
        Assertions.assertThat(form.judgeTeams).isEmpty()
    }
}