import odyseja.odysejapka.gad.TeamsGroupKey
import odyseja.odysejapka.timetable.Performance
import org.apache.commons.lang3.StringUtils
import kotlin.math.abs
import kotlin.random.Random

data class Team(
    val performanceHour: String,
    val spontanHour: String,
    val code: String,
    val membershipNumber: String,
    val league: String,
    val part: String,
    val teamName: String,
    val shortTeamName: String,
    val city: String,
    val zspRow: Int,
    val day: String,
    val stage: Int,
    var zspSheet: String?,
    var longTermScore: Float?,
    var styleScore: Float?,
    var penaltyScore: Float?,
    var weightHeld: Float?,
    var spontaneousScore: Float?,
    var ranatra: Boolean
) {
    fun getFileName(): String {
        return "$`code`_$teamName"
    }

    fun getAge(): String {
        val code = code[3].toString()
        if (code == "J") {
            return "0"
        }
        return code
    }

    fun getProblem(): String {
        val problem = code[1].toString()
        if (problem == "J") {
            return "0"
        }
        return problem
    }

    fun getSpontanDay(): String {
        if (day.lowercase().contains("sobota")) {
            return "niedziela"
        }
        return "sobota"
    }

    fun toPerformance(city: String): Performance {
        return Performance(
            0,
            city,
            teamName,
            getProblem().toInt(),
            getAge().toInt(),
            stage,
            performanceHour,
            spontanHour,
            getPart(),
            day.lowercase(),
            getSpontanDay(),
            getFormattedLeague(),
            zspRow = zspRow,
            zspSheet = zspSheet,
            performanceDate = null
        )
    }

    private fun getPart(): Int {
        if (part.isBlank()) {
            return 0
        }
        return part.toInt()
    }

    fun getCity(teamName: String): String {
        val teamAndCity = teamName.split("-")
        return if (teamAndCity.size > 1) {
            teamAndCity[teamAndCity.size - 1]
        } else "-"
    }

    fun isOutsideTournament(): Boolean {
        return isJunior() || isForeigner()
    }

    fun isJunior(): Boolean {
        return code[1] == 'J'
    }

    fun getGroup(): TeamsGroupKey {
        return TeamsGroupKey(getProblem().toInt(), getAge().toInt(), getFormattedLeague())
    }

    private fun getFormattedLeague(): String {
        return if (league == "0" || league.isBlank()) "" else league
    }

    fun getAbsPenaltyScore(): Float {
        if (penaltyScore != null) {
            return abs(penaltyScore!!) // [*] pamiętamy
        }

        return 0F
    }

    fun isBalsa(): Boolean {
        return code.substring(1, 2) == "4"
    }

    fun getProblemLeague(): String {
        if (league == "0") return code.substring(3, 4)
        return "${code.substring(3, 4)}${league}"
    }

    fun isForeign(): Boolean {
        return membershipNumber == ""
    }

    fun getTmRow(): String {
        val adjustedMemNbr = Random.nextInt(0, 99999)
        return "${getProblem()},${getProblemLeague()},${adjustedMemNbr},${removePolishChars(shortTeamName)},${
            removePolishChars(
                city
            )
        },${getBalsaScore()},${getLongTermScore()},${styleScore},${spontaneousScore},${getAbsPenaltyScore()},${adjustedMemNbr}"
    }

    private fun removePolishChars(text: String): String {
        return StringUtils.stripAccents(text)
    }

    private fun getBalsaScore(): String {
        if (!isBalsa()) {
            return longTermScore.toString()
        }
        return weightHeld.toString()
    }

    private fun getLongTermScore(): String {
        if (!isBalsa()) {
            return ""
        }
        return longTermScore.toString()
    }

    fun spontanSort(): String {
        val day = if (getSpontanDay().lowercase().contains("sobota")) "0" else "1"
        return "$day$spontanHour"
    }

    fun isForeigner(): Boolean {
        for (foreigner in listOf("spain", "china", "slovakia")) {
            if (city.lowercase().contains(foreigner) || teamName.lowercase().contains(foreigner)) {
                return true
            }
        }
        return false
    }
}
