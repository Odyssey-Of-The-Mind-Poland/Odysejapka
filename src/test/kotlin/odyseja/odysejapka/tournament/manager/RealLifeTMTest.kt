package odyseja.odysejapka.tournament.manager

import Team
import com.opencsv.bean.CsvToBeanBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Stream
import kotlin.math.abs

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RealLifeTMTest {

    private val tmCalculator = TMCalculator()

    companion object {
        @JvmStatic
        fun csvPathsProvider(): Stream<String> {
            return Stream.of(
                "/TM-test-cases/skawina2025.csv",
                "/TM-test-cases/bialystok2025.csv",
                "/TM-test-cases/warszawa2025.csv",
                "/TM-test-cases/poznan2025.csv",
            )
        }
    }

    @Order(1)
    @ParameterizedTest(name = "Checking CSV: {0}")
    @MethodSource("csvPathsProvider")
    fun `compare TM Calculator with real TM results`(csvResourcePath: String) {
        val csvStream = this::class.java.getResourceAsStream(csvResourcePath)
            ?: fail("Could not find '$csvResourcePath' in resources")

        val csvRows: List<TeamCsvRow> = BufferedReader(InputStreamReader(csvStream)).use { reader ->
            CsvToBeanBuilder<TeamCsvRow>(reader)
                .withType(TeamCsvRow::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse()
        }

        val teams = csvRows.map { row ->
            Team(
                performanceHour = "",
                spontanHour = "",
                code = "P${row.problem}G${row.division}",
                membershipNumber = row.membershipNumber,
                league = "",
                part = "",
                teamName = row.teamName,
                shortTeamName = row.teamName,
                city = row.city,
                zspRow = 0,
                day = "",
                stage = 0,
                zspSheet = "",
                longTermScore = row.longTermRaw,
                spontaneousScore = row.spontaneousRaw,
                styleScore = row.styleRaw,
                penaltyScore = row.penalty,
                weightHeld = row.weightHeld
            )
        }

        val finalScoreGroups = tmCalculator.calculateScores(teams)

        val finalScoresByKey = mutableMapOf<String, FinalTeamScore>()
        finalScoreGroups.forEach { group ->
            group.teamScores.forEach { score ->
                val key = "${group.problem}-${group.division}-${score.teamName}"
                finalScoresByKey[key] = score
            }
        }

        for (row in csvRows) {
            val key = "${row.problem}-${row.division}-${row.teamName}"
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
        }
    }
}
