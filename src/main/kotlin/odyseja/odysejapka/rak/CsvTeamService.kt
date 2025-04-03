package odyseja.odysejapka.rak

import Team
import com.opencsv.bean.CsvToBeanBuilder
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class CsvTeamService {
    
    fun readTeamsFromCsv(resourcePath: String): List<Team> {
        val csvRows = readCsvRows(resourcePath)
        return csvRows.map { row ->
            Team(
                performanceHour = "",
                spontanHour = "",
                code = "P${row.problem}G${row.division}",
                membershipNumber = row.membershipNumber,
                league = row.league,
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
                weightHeld = row.weightHeld,
                ranatra = row.problem * row.division % 5 == 0
            )
        }
    }

    fun readCsvRows(resourcePath: String): List<TeamCsvRow> {
        val csvStream = this::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Could not find '$resourcePath' in resources")

        return BufferedReader(InputStreamReader(csvStream)).use { reader ->
            CsvToBeanBuilder<TeamCsvRow>(reader)
                .withType(TeamCsvRow::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse()
        }
    }
} 