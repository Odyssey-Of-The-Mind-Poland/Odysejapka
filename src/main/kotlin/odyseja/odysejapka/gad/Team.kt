import odyseja.odysejapka.timetable.Performance

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
    var spontaneousScore: Float?
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
        val code = code[1].toString()
        if (code == "J") {
            return "0"
        }
        return code
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
            part.toInt(),
            day.lowercase(),
            getSpontanDay(),
            if (league != "0") league else "",
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

    fun isJunior(): Boolean {
        return code[1] == 'J'
    }
}
