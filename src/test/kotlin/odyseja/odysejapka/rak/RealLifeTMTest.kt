package odyseja.odysejapka.rak

import Team
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.abs

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RealLifeTMTest {

    private val rakCalculator = RakCalculator()
    private val csvTeamService = CsvTeamService()

    companion object {
        @JvmStatic
        fun csvPathsProvider(): Stream<String> {
            return Stream.of(
                "/TM-test-cases/skawina2025.csv",
                "/TM-test-cases/bialystok2025.csv",
                "/TM-test-cases/warszawa2025.csv",
                "/TM-test-cases/poznan2025.csv",
                "/TM-test-cases/final2024.csv",
            )
        }
    }

    @Order(1)
    @ParameterizedTest(name = "Checking CSV: {0}")
    @MethodSource("csvPathsProvider")
    fun `compare TM Calculator with real TM results`(csvResourcePath: String) {
        val teams = csvTeamService.readTeamsFromCsv(csvResourcePath)
        val finalScoreGroups = rakCalculator.calculateScores(teams)

        val csvRows = csvTeamService.readCsvRows(csvResourcePath)

        val finalScoresByKey = mutableMapOf<String, FinalTeamScore>()
        finalScoreGroups.forEach { group ->
            group.teamScores.forEach { score ->
                val key = "${group.problem}-${group.division}-${group.league}-${score.team.city}-${score.teamName}"
                finalScoresByKey[key] = score
            }
        }

        for (row in csvRows) {
            val key = "${row.problem}-${row.division}-${row.league}-${row.city}-${row.teamName}"
            val finalScore = finalScoresByKey[key]
                ?: fail("No computed score found for team '$key'")

            val diffLT = abs(finalScore.longTermScore - row.expectedLongTerm)
            val diffSt = abs(finalScore.styleScore - row.expectedStyle)
            val diffSp = abs(finalScore.spontaneousScore - row.expectedSpont)
            val diffBa = abs(finalScore.balsaScore - row.expectedBalsa)
            val diffTot = abs(finalScore.total - row.expectedTotal)

            assertTrue(
                diffLT < 0.5,
                "Team $key LongTerm => expected ${row.expectedLongTerm}, got ${finalScore.longTermScore} (diff=$diffLT)"
            )
            assertTrue(
                diffSt < 0.5,
                "Team $key Style => expected ${row.expectedStyle}, got ${finalScore.styleScore} (diff=$diffSt)"
            )
            assertTrue(
                diffSp < 0.5,
                "Team $key Spont => expected ${row.expectedSpont}, got ${finalScore.spontaneousScore} (diff=$diffSp)"
            )
            assertTrue(
                diffBa < 0.5,
                "Team $key Balsa => expected ${row.expectedBalsa}, got ${finalScore.balsaScore} (diff=$diffBa)"
            )
            assertTrue(
                diffTot < 0.5,
                "Team $key Total => expected ${row.expectedTotal}, got ${finalScore.total} (diff=$diffTot)"
            )
            assertEquals(row.expectedPlace, finalScore.place, "Team $key has unexpected place")
        }
    }
}
