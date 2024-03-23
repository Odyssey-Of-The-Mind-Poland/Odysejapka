import odyseja.odysejapka.domain.Performance
import org.checkerframework.checker.lock.qual.LockHeld
import javax.persistence.Column

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
    var longTermScore: String?,
    var styleScore: String?,
    var penaltyScore: String?,
    var weightHeld: String?,
    var spontaneousScore: String?
) {
    fun getFileName(): String {
        return "$`code`_$teamName"
    }

    fun getAge(): String {
        return code[3].toString()
    }

    fun getProblem(): String {
        return code[1].toString()
    }

    fun getSpontanDay(): String {
        if (day.lowercase().contains("sobota")) {
            return "sobota"
        }
        return "niedziela"
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
            part.toInt(),
            day,
            getSpontanDay(),
            league,
            zspRow = zspRow,
            zspSheet = zspSheet
        )
    }
    fun getCity(teamName: String):String{
        val teamAndCity = teamName.split("-")
        return if(teamAndCity.size > 1){
            teamAndCity[teamAndCity.size-1]
        }
        else "-"
    }
}
