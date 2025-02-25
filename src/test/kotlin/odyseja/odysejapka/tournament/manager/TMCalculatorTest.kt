package odyseja.odysejapka.tournament.manager

import Team
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class TMCalculatorTest {
    private var tmCalculator: TMCalculator = TMCalculator()


    private fun generate_example_team(longTermScore: Float? = null, spontaneousScore: Float? = null, styleScore: Float? = null, penalty: Float? = null, teamName: String? = null): Team {
        return Team(
                performanceHour = "1000",
                spontanHour = "1000",
                code = "P1G1",
                membershipNumber = "11111",
                league = "",
                part = "0",
                teamName = teamName ?: "ABC",
                shortTeamName = teamName ?: "ABC",
                city = "Gdansk",
                zspRow = 1,
                day = "sobota",
                stage = 1,
                zspSheet = "test",
                longTermScore = longTermScore ?: 0.0f,
                spontaneousScore = spontaneousScore ?: 0.0f,
                styleScore = styleScore ?: 0.0f,
                penaltyScore = penalty ?: 0.0f,
                weightHeld = 0.0f
        )
    }

    @Test
    fun `test it`() {
        val teams = listOf(
                generate_example_team(
                        longTermScore = 150.0f,
                        spontaneousScore = 66.67f,
                        styleScore = 35.0f,
                        penalty = 0.0f,
                        teamName="A"
                ),
                generate_example_team(
                        longTermScore = 120.0f,
                        spontaneousScore = 65.67f,
                        styleScore = 30.0f,
                        penalty = 0.0f,
                        teamName="B"
                )



        )
        val result = tmCalculator.calculateScores(teams)
        val expectedResult = FinalScoreGroup(
                problem = 1,
                division = 1,
                league = 0,
                teamScores = listOf(
                        FinalTeamScore(
                                place = 1,
                                longTermScore = 200.0,
                                balsaScore = 100.0,
                                styleScore = 50.0,
                                penalty = 0.0,
                                teamName = "A"
                        ),
                        FinalTeamScore(
                                place = 2,
                                longTermScore = 0.0, // todo fix vals
                                balsaScore = 0.0,
                                styleScore = 0.0,
                                penalty = 0.0,
                                teamName = "B"
                        )
                )
        )

        assertArrayEquals(expectedResult.teamScores.toTypedArray(), result[0].teamScores.toTypedArray())

    }


}