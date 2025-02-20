package odyseja.odysejapka.tournament.manager

import Team
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset
import kotlin.math.abs


@Service
class TournamentManagerService {

    fun OutputStream.writeCsv(teams: List<Team>) {
        val writer = bufferedWriter()
        writer.write("""Problem,Division,Number,Name,City,Raw_longt1,Raw_longt2,Raw_style,Raw_spont,Penalty""")
        writer.newLine()
        println("${teams.size} teams in total")
        println("${teams.filter { it.isJunior() }.size} junior teams")
        println("${teams.filter { it.isForeign() }.size} foreign teams")

        for (team in teams) {
            if (team.isJunior() || team.isForeign()) {
                println("Skipping ${team.teamName}")
                continue
            }

            writer.write(team.getTmRow())

            writer.newLine()
        }

        writer.flush()
    }

    fun generateCsv(zspId: String): ByteArray {
        val sheetsAdapter = ZspSheetsAdapter.getZspSheetsAdapter(zspId)
        val sheets = sheetsAdapter.getSheets()
        val byteArrayOutputStream = ByteArrayOutputStream()

        val allTeams: MutableList<Team> = mutableListOf()
        for (sheet in sheets!!) {
            val title = sheet.properties.title
            println("Processing sheet: $title")
            val teams = sheetsAdapter.getTeams(title)
            val teamsFromSheet = teams.teams
            allTeams.addAll(teamsFromSheet)
        }
        byteArrayOutputStream.use { it.writeCsv(allTeams) }
        return byteArrayOutputStream.toString(Charset.forName("UTF-8")).toByteArray()

    }
}