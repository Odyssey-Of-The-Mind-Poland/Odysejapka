package odyseja.odysejapka.tournament.manager

import com.opencsv.bean.CsvBindByName

data class TeamCsvRow(
    @CsvBindByName(column = "problem")
    val problem: Int = 0,

    @CsvBindByName(column = "division")
    val division: Int = 0,

    @CsvBindByName(column = "membershipNumber")
    val membershipNumber: String = "",

    @CsvBindByName(column = "teamName")
    val teamName: String = "",

    @CsvBindByName(column = "city")
    val city: String = "",

    @CsvBindByName(column = "weightHeld")
    val weightHeld: Float = 0.0f,

    @CsvBindByName(column = "longTermRaw")
    val longTermRaw: Float = 0.0f,

    @CsvBindByName(column = "styleRaw")
    val styleRaw: Float = 0.0f,

    @CsvBindByName(column = "spontaneousRaw")
    val spontaneousRaw: Float = 0.0f,

    @CsvBindByName(column = "penalty")
    val penalty: Float = 0.0f,

    @CsvBindByName(column = "expectedLongTerm")
    val expectedLongTerm: Double = 0.0,

    @CsvBindByName(column = "expectedStyle")
    val expectedStyle: Double = 0.0,

    @CsvBindByName(column = "expectedSpont")
    val expectedSpont: Double = 0.0,

    @CsvBindByName(column = "expectedBalsa")
    val expectedBalsa: Double = 0.0,

    @CsvBindByName(column = "expectedTotal")
    val expectedTotal: Double = 0.0
)
