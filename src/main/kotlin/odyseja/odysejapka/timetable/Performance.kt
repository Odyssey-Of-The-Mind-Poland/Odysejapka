package odyseja.odysejapka.timetable

import com.opencsv.bean.CsvBindByName
import java.time.LocalDate

data class Performance (
    val id: Int,
    @CsvBindByName(column = "Konkurs")
    val city: String,
    @CsvBindByName(column = "Drużyna")
    val team: String,
    @CsvBindByName(column = "Problem")
    val problem: Int,
    @CsvBindByName(column = "Grupa wiekowa")
    val age: Int,
    @CsvBindByName(column = "Scena")
    val stage: Int,
    @CsvBindByName(column = "Godzina występu")
    val performance: String,
    @CsvBindByName(column = "Godzina spontana")
    val spontan: String,
    @CsvBindByName(column = "Część")
    val part: Int? = null,
    @CsvBindByName(column = "Dzień występu")
    var performanceDay: String,
    @CsvBindByName(column = "Dzień spontana")
    var spontanDay: String,
    @CsvBindByName(column = "Liga")
    var league: String? = null,
    var zspRow: Int? = null,
    var zspSheet: String? = null,
    var performanceDate: LocalDate? = null
) {


}