package odyseja.odysejapka.rak

import Team
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RakCalculatorTest {

    private var rakCalculator: RakCalculator = RakCalculator()

    @Test
    fun `score single team`() {
        val teams = listOf(
            createTeam(
                longTermScore = 100.0f,
                spontaneousScore = 50.0f,
                styleScore = 30.0f,
                penalty = 0.0f,
                teamName = "Solo"
            )
        )

        val results = rakCalculator.calculateScores(teams)

        val expected = listOf(
            FinalScoreGroup(
                problem = 1,
                division = 1,
                league = "",
                teamScores = listOf(
                    FinalTeamScore(
                        place = 1,
                        teamName = "Solo",
                        longTermScore = 200.0,
                        spontaneousScore = 100.0,
                        styleScore = 50.0,
                        balsaScore = 0.0,
                        penalty = 0.0,
                        total = 350.0,
                        team = teams[0],
                    )
                )
            )
        )

        assertEquals(expected, results)
    }

    @Test
    fun `score single balsa team`() {
        val teams = listOf(
            createTeam(
                problem = 4,
                weightHeld = 10.0f,
                longTermScore = 100.0f,
                spontaneousScore = 50.0f,
                styleScore = 30.0f,
                penalty = 0.0f,
                teamName = "Solo"
            )
        )

        val results = rakCalculator.calculateScores(teams)

        val expected = listOf(
            FinalScoreGroup(
                problem = 4,
                division = 1,
                league = "",
                teamScores = listOf(
                    FinalTeamScore(
                        place = 1,
                        teamName = "Solo",
                        longTermScore = 200.0,
                        spontaneousScore = 100.0,
                        styleScore = 50.0,
                        balsaScore = 100.0,
                        penalty = 0.0,
                        total = 350.0,
                        team = teams[0]
                    )
                )
            )
        )

        assertEquals(expected, results)
    }


    @Test
    fun `score two teams no tie`() {
        val teams = listOf(
            createTeam(
                longTermScore = 150.0f,
                spontaneousScore = 66.67f,
                styleScore = 35.0f,
                teamName = "Team A"
            ),
            createTeam(
                longTermScore = 120.0f,
                spontaneousScore = 65.67f,
                styleScore = 30.0f,
                teamName = "Team B"
            )
        )

        val result = rakCalculator.calculateScores(teams)

        val expected = listOf(
            FinalScoreGroup(
                problem = 1,
                division = 1,
                league = "0",
                teamScores = listOf(
                    FinalTeamScore(
                        place = 1,
                        teamName = "Team A",
                        longTermScore = 200.0,
                        spontaneousScore = 100.0,
                        styleScore = 50.0,
                        balsaScore = 0.0,
                        penalty = 0.0,
                        total = 0.0,
                        team = teams[0]
                    ),
                    FinalTeamScore(
                        place = 2,
                        teamName = "Team B",
                        longTermScore = 160.0,
                        spontaneousScore = 98.5,
                        styleScore = 42.8571428571,
                        balsaScore = 0.0,
                        penalty = 0.0,
                        total = 0.0,
                        team = teams[1]
                    )
                )
            )
        )
        assertEquals(expected[0].problem, result[0].problem)
        assertEquals(expected[0].division, result[0].division)
    }

    @Test
    fun `score two teams tie`() {
        val teams = listOf(
            createTeam(
                longTermScore = 140.0f,
                spontaneousScore = 66.0f,
                styleScore = 35.0f,
                teamName = "Tied A"
            ),
            createTeam(
                longTermScore = 139.5f,
                spontaneousScore = 66.0f,
                styleScore = 35.0f,
                teamName = "Tied B"
            )
        )

        val result = rakCalculator.calculateScores(teams)

        assertEquals(1, result[0].teamScores[0].place)
        assertEquals(1, result[0].teamScores[1].place)
    }

    @Test
    fun `score multiple teams with ties`() {
        val teams = listOf(
            createTeam(longTermScore = 150f, spontaneousScore = 70f, styleScore = 35f, teamName = "Team 1"),
            createTeam(longTermScore = 149.5f, spontaneousScore = 70f, styleScore = 35f, teamName = "Team 2"),
            createTeam(longTermScore = 149.6f, spontaneousScore = 70f, styleScore = 35f, teamName = "Team 3"),
            createTeam(longTermScore = 140f, spontaneousScore = 69f, styleScore = 34f, teamName = "Team 4")
        )

        val result = rakCalculator.calculateScores(teams)

        assertTrue(result.isNotEmpty())
        assertEquals(1, result[0].teamScores[0].place)
        assertEquals(1, result[0].teamScores[1].place)
        assertEquals(1, result[0].teamScores[2].place)
        assertEquals(2, result[0].teamScores[3].place)
    }

    @Test
    fun `score teams with penalties`() {
        val teams = listOf(
            createTeam(longTermScore = 150f, spontaneousScore = 70f, styleScore = 35f, penalty = 10f, teamName = "Penalized"),
            createTeam(longTermScore = 140f, spontaneousScore = 65f, styleScore = 30f, penalty = 0f, teamName = "Clean")
        )

        val result = rakCalculator.calculateScores(teams)

        val penalizedScore = result[0].teamScores.first { it.teamName == "Penalized" }
        val cleanScore     = result[0].teamScores.first { it.teamName == "Clean" }

        assertEquals(10.0, penalizedScore.penalty, 0.001)
    }


    @Test
    fun `score multiple problems divisions`() {
        val teams = listOf(
            // Problem 1, Division 1
            createTeam(problem = 1, division = 1, longTermScore = 100f, spontaneousScore = 50f, styleScore = 25f, teamName = "P1D1-A"),
            createTeam(problem = 1, division = 1, longTermScore = 80f, spontaneousScore = 50f, styleScore = 25f, teamName = "P1D1-B"),

            // Problem 1, Division 2
            createTeam(problem = 1, division = 2, longTermScore = 200f, spontaneousScore = 60f, styleScore = 30f, teamName = "P1D2-A"),
            createTeam(problem = 1, division = 2, longTermScore = 180f, spontaneousScore = 60f, styleScore = 30f, teamName = "P1D2-B"),

            // Problem 2, Division 1
            createTeam(problem = 2, division = 1, longTermScore = 90f, spontaneousScore = 45f, styleScore = 20f, teamName = "P2D1-A"),
            createTeam(problem = 2, division = 1, longTermScore = 90f, spontaneousScore = 45f, styleScore = 20f, teamName = "P2D1-B"), // identical
        )

        val result = rakCalculator.calculateScores(teams)

        assertEquals(3, result.size)

        val p1d1Group = result.find { it.problem == 1 && it.division == 1 }
        val p1d2Group = result.find { it.problem == 1 && it.division == 2 }
        val p2d1Group = result.find { it.problem == 2 && it.division == 1 }

        assertEquals(2, p1d1Group?.teamScores?.size)
        assertEquals(2, p1d2Group?.teamScores?.size)
        assertEquals(2, p2d1Group?.teamScores?.size)
    }

    @Test
    fun `score teams with zero raw scores`() {
        val teams = listOf(
            createTeam(longTermScore = 0f, spontaneousScore = 0f, styleScore = 0f, teamName = "Zero A"),
            createTeam(longTermScore = 0f, spontaneousScore = 0f, styleScore = 0f, teamName = "Zero B")
        )

        val result = rakCalculator.calculateScores(teams)

        assertEquals(1, result.size)
        assertEquals(2, result[0].teamScores.size)

        assertEquals(1, result[0].teamScores[0].place)
        assertEquals(1, result[0].teamScores[1].place)
    }

    @Test
    fun `score identical teams`() {
        val teams = listOf(
            createTeam(longTermScore = 100f, spontaneousScore = 40f, styleScore = 25f, teamName = "Same A"),
            createTeam(longTermScore = 100f, spontaneousScore = 40f, styleScore = 25f, teamName = "Same B"),
            createTeam(longTermScore = 100f, spontaneousScore = 40f, styleScore = 25f, teamName = "Same C")
        )

        val result = rakCalculator.calculateScores(teams)

        assertTrue(result.isNotEmpty())
        val group = result[0]
        assertEquals(3, group.teamScores.size)
        group.teamScores.forEach {
            assertEquals(1, it.place)
        }
    }


    @Test
    fun `score three teams with the difference near 1 0`() {
        val teams = listOf(
            createTeam(longTermScore = 145f, spontaneousScore = 68f, styleScore = 35f, teamName = "High 319.27"),
            createTeam(longTermScore = 144.5f, spontaneousScore = 68f, styleScore = 35f, teamName = "Second 318.75"),
            createTeam(longTermScore = 144f, spontaneousScore = 68f, styleScore = 35f, teamName = "Third 318.25"),
        )

        val results = rakCalculator.calculateScores(teams)

        val scores = results[0].teamScores.sortedByDescending { it.longTermScore + it.spontaneousScore + it.styleScore }

        assertEquals(1, scores[0].place)
        assertEquals(1, scores[1].place)
        assertEquals(2, scores[2].place)
    }

    @Test
    fun `score two balsa teams from screenshot`() {
        val teams = listOf(
            createTeam(
                problem = 4,
                division = 1,
                teamName = "Spoleczna Szkola Podstawowa nr 3 STO",
                weightHeld = 42.50f,
                longTermScore = 78.0f,
                styleScore = 29.0f,
                spontaneousScore = 68.33f,
                penalty = 0.0f
            ),

            createTeam(
                problem = 4,
                division = 1,
                teamName = "Szkola Podstawowa nr 68",
                weightHeld = 12.50f,
                longTermScore = 62.50f,
                styleScore = 31.0f,
                spontaneousScore = 66.67f,
                penalty = 0.0f
            )
        )

        val results = rakCalculator.calculateScores(teams)

        assertEquals(1, results.size)
        val group = results[0]
        assertEquals(4, group.problem)
        assertEquals(1, group.division)
        assertEquals(2, group.teamScores.size)

        val sortedByTotal = group.teamScores.sortedByDescending { it.total }

        val team1 = sortedByTotal[0]
        assertEquals("Spoleczna Szkola Podstawowa nr 3 STO", team1.teamName)
        assertEquals(1, team1.place, "First team should be in 1st place.")

        assertEquals(346.77, team1.total, 0.01)

        val team2 = sortedByTotal[1]
        assertEquals("Szkola Podstawowa nr 68", team2.teamName)
        assertEquals(2, team2.place, "Second team should be in 2nd place.")

        assertEquals(257.11, team2.total, 0.01)
    }


    private fun createTeam(
        longTermScore: Float? = null,
        spontaneousScore: Float? = null,
        styleScore: Float? = null,
        penalty: Float? = null,
        teamName: String? = null,
        problem: Int = 1,
        division: Int = 1,
        league: String = "0",
        weightHeld: Float = 0.0f,
    ): Team {
        return Team(
            performanceHour = "1000",
            spontanHour = "1000",
            code = "P${problem}G${division}",
            membershipNumber = "11111",
            league = league,
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
            weightHeld = weightHeld,
            ranatra = false

        )
    }
}
