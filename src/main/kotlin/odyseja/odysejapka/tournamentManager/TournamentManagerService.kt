package odyseja.odysejapka.tournamentManager

import Team
import com.google.api.client.json.gson.GsonFactory
import odyseja.odysejapka.drive.CredentialsProvider
import odyseja.odysejapka.drive.ZspSheetsAdapter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset


@Service
class TournamentManagerService {

    private fun isJuniorTeam(code: String): Boolean{
        return code.substring(1,2) == "J" && code.substring(3,4) == "J"
    }

    fun OutputStream.writeCsv(teams: List<Team>) {
        val writer = bufferedWriter()
        writer.write("""Problem,Division,Number,Name,City,Raw_longt1,Raw_longt2,Raw_style,Raw_spont,Penalty""")
        writer.newLine()
        teams.forEach {
            // Junior teams and guest teams from other countries should not be imported
            if (!isJuniorTeam(code=it.code) && it.membershipNumber != ""){
                var problemLeague = ""
                if (it.league != "0") problemLeague = "${it.code.substring(3,4)}${it.league}"
                else problemLeague = it.code.substring(3,4)
                if (it.code.substring(1,2) == "4")
                    writer.write("${it.code.substring(1,2)},${problemLeague},${it.membershipNumber},${it.shortTeamName},${it.city},${it.weightHeld},${it.longTermScore},${it.styleScore},${it.spontaneousScore},${it.penaltyScore}")
                else
                    writer.write("${it.code.substring(1,2)},${problemLeague},${it.membershipNumber},${it.shortTeamName},${it.city},${it.longTermScore},,${it.styleScore},${it.spontaneousScore},${it.penaltyScore}")

                writer.newLine()

            }

        }
        writer.flush()
    }

    fun generateCsv(zspId: String): ByteArray {
        val credentials = CredentialsProvider().getCredentials()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val sheetsAdapter = ZspSheetsAdapter(credentials, jsonFactory, zspId)
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