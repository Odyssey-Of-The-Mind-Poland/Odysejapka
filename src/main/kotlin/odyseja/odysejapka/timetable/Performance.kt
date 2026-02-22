package odyseja.odysejapka.timetable

import com.opencsv.bean.CsvBindByName
import java.time.LocalDate

data class Performance (
    val id: Int = 0,
    @CsvBindByName(column = "Konkurs")
    val city: String = "",
    @CsvBindByName(column = "Drużyna", required = true)
    val team: String = "",
    @CsvBindByName(column = "Problem", required = true)
    val problem: Int = 0,
    @CsvBindByName(column = "Grupa wiekowa", required = true)
    val age: Int = 0,
    @CsvBindByName(column = "Scena", required = true)
    val stage: Int = 0,
    @CsvBindByName(column = "Godzina występu", required = true)
    var performance: String = "",
    @CsvBindByName(column = "Godzina spontana", required = true)
    var spontan: String = "",
    @CsvBindByName(column = "Część", required = false)
    val part: Int? = null,
    @CsvBindByName(column = "Dzień występu", required = true)
    var performanceDay: String = "",
    @CsvBindByName(column = "Dzień spontana", required = true)
    var spontanDay: String = "",
    @CsvBindByName(column = "Liga", required = false)
    var league: String? = null,
    var zspRow: Int? = null,
    var zspSheet: String? = null,
    var performanceDate: LocalDate? = null
) {
    fun standardize() {
        spontanDay = spontanDay.lowercase()
        performanceDay = performanceDay.lowercase()
        val shortTimeRegex = Regex("\\d:\\d{2}")
        if (shortTimeRegex.matches(spontan)) spontan = "0${spontan}"
        if (shortTimeRegex.matches(performance)) performance = "0${performance}"
    }

    fun validate() {
        require(problem in 0..5) {"Numer problemu musi wynosić od 0 do 5."}
        require(age in 0..4) {"Numer grupy wiekowej musi wynosić od 0 do 4."}
        require(Regex("\\d{2}:\\d{2}").matches(spontan)) {"Godzina spontana powinna być w następującym formacie: 08:45."}
        require(Regex("\\d{2}:\\d{2}").matches(performance)) {"Godzina występu powinna być w następującym formacie: 08:45."}
        require(spontanDay in arrayOf("sobota", "niedziela")) {"Dozwolone dni spontana to sobota lub niedziela."}
        require(performanceDay in arrayOf("sobota", "niedziela")) {"Dozwolone dni występu to sobota lub niedziela."}
    }

}