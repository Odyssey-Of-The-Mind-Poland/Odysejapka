package odyseja.odysejapka.rak

import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset


@Service
class CsvService {

    fun generateCsv(zspId: String): ByteArray {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val byteArrayOutputStream = ByteArrayOutputStream()

        byteArrayOutputStream.use { it.writeCsv(RakCalculator().calculateScores(sheetsAdapter.getAllTeams())) }
        return byteArrayOutputStream.toString(Charset.forName("UTF-8")).toByteArray()

    }

    private fun OutputStream.writeCsv(teams: List<FinalScoreGroup>) {
        val writer = bufferedWriter()

        println("${teams.size} teams in total")


        for (group in teams) {

            writer.write("""Problem,grupa wiekowa,liga""")
            writer.newLine()

            writer.write("${group.problem},${group.division},${group.league}")
            writer.newLine()

            writer.write("""Miejsce,druzyna,kod""")
            writer.newLine()
            for (score in group.teamScores) {

                if (score.team.isOutsideTournament()) {
                    println("Skipping ${score.team.teamName}")
                    continue
                }
                writer.write(
                    "${score.place},${score.team.teamName},${score.team.code}"
                )
                writer.newLine()
            }
        }

        writer.flush()
    }

}