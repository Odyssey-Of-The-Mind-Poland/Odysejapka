import odyseja.odysejapka.domain.Performance

data class Team(
    val performanceHour: String,
    val spontanHour: String,
    val code: String,
    val league: String,
    val part: String,
    val teamName: String,
    val zspRow: Int,
    val day: String,
    val stage: Int
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
            league
        )
    }
}
