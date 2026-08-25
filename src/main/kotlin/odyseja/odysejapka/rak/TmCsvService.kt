package odyseja.odysejapka.rak

import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset


@Service
class TmCsvService {

    fun generateCsv(zspId: String): ByteArray {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val teams = sheetsAdapter.getAllTeams()
        val scoreGroups = RakCalculator().calculateScores(teams)
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.use { it.writeCsv(scoreGroups) }
        return byteArrayOutputStream.toString(Charset.forName("UTF-8")).toByteArray()
    }

    private fun OutputStream.writeCsv(scoreGroups: List<FinalScoreGroup>) {
        val writer = bufferedWriter()
        writer.write("Place,TeamName,City,Problem,Age,League,Total,LongTerm,Style,Spontaneous,Balsa,Penalty,Raw_LongTerm,Raw_Style,Raw_Spontaneous,Raw_WeightHeld")
        writer.newLine()

        for (group in scoreGroups) {
            for (ts in group.teamScores) {
                val cols = listOf(
                    ts.place,
                    ts.team.shortTeamName.escapeCsv(),
                    ts.team.city.escapeCsv(),
                    group.problem,
                    group.division,
                    group.league.escapeCsv(),
                    ts.total,
                    ts.longTermScore,
                    ts.styleScore,
                    ts.spontaneousScore,
                    ts.balsaScore,
                    ts.penalty,
                    ts.team.longTermScore ?: "",
                    ts.team.styleScore ?: "",
                    ts.team.spontaneousScore ?: "",
                    ts.team.weightHeld ?: ""
                )
                writer.write(cols.joinToString(","))
                writer.newLine()
            }
        }

        writer.flush()
    }

    private fun String.escapeCsv(): String =
        if (contains(',') || contains('"') || contains('\n')) "\"${replace("\"", "\"\"")}\"" else this
}
