package odyseja.odysejapka.timetable

import com.opencsv.bean.CsvBindByName
import java.time.LocalDate

data class Performance (
    val id: Int = 0,
    @CsvBindByName(column = "Konkurs")
    val city: String = "",
    @CsvBindByName(column = "Drużyna")
    val team: String = "",
    @CsvBindByName(column = "Problem")
    val problem: Int = 0,
    @CsvBindByName(column = "Grupa wiekowa")
    val age: Int = 0,
    @CsvBindByName(column = "Scena")
    val stage: Int = 0,
    @CsvBindByName(column = "Godzina występu")
    val performance: String = "",
    @CsvBindByName(column = "Godzina spontana")
    val spontan: String = "",
    @CsvBindByName(column = "Część")
    val part: Int? = null,
    @CsvBindByName(column = "Dzień występu")
    var performanceDay: String = "",
    @CsvBindByName(column = "Dzień spontana")
    var spontanDay: String = "",
    @CsvBindByName(column = "Liga")
    var league: String? = null,
    var zspRow: Int? = null,
    var zspSheet: String? = null,
    var performanceDate: LocalDate? = null
) {


}