package odyseja.odysejapka.tournamentManager

import Team
import com.google.api.client.json.gson.GsonFactory
import odyseja.odysejapka.drive.CredentialsProvider
import odyseja.odysejapka.drive.SpreadSheetsAdapter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset


@Service
class TournamentManagerService {

    fun OutputStream.writeCsv(movies: List<Team>) {
        val writer = bufferedWriter()
        writer.write("""Problem, Division, Number, Name, City, Region, Raw_longt1, Raw_longt2, Raw_style, Raw_spont, Penalty""")
        writer.newLine()
        movies.forEach {
            writer.write("${it.code.substring(1,2)}, ${it.code.substring(3,4)},${it.membershipNumber},${it.teamName},${it.getCity(it.teamName)} ,-, ${it.longTermScore}, -, ${it.styleScore}, ${it.spontaneousScore}, ${it.penaltyScore}")
            writer.newLine()
        }
        writer.flush()
    }

    fun generateCsv(zspId: String): ByteArray {
        val credentials = CredentialsProvider().getCredentials()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val sheetsAdapter = SpreadSheetsAdapter(credentials, jsonFactory, zspId)
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