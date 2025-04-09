package odyseja.odysejapka.rak

import Team
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset


@Service
class TmCsvService {

    fun generateCsv(zspId: String): ByteArray {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val byteArrayOutputStream = ByteArrayOutputStream()

        byteArrayOutputStream.use { it.writeCsv(sheetsAdapter.getAllTeams()) }
        return byteArrayOutputStream.toString(Charset.forName("UTF-8")).toByteArray()

    }

    private fun OutputStream.writeCsv(teams: List<Team>) {
        val writer = bufferedWriter()
        writer.write("""Problem,Division,Number,Name,City,Raw_longt1,Raw_longt2,Raw_style,Raw_spont,Penalty,memberNbr""")
        writer.newLine()
        println("${teams.size} teams in total")
        println("${teams.filter { it.isJunior() }.size} junior teams")
        println("${teams.filter { it.isForeign() }.size} foreign teams")

        for (team in teams) {
            if (team.isOutsideTournament()) {
                println("Skipping ${team.teamName}")
                continue
            }

            writer.write(team.getTmRow())

            writer.newLine()
        }

        writer.flush()
    }

}