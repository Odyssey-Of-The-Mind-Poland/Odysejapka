package odyseja.odysejapka.form

enum class SubjectiveRanges(
    val displayName: String,
    val foRanges: FoRanges,
    val frValues: FrValues
) {
    ONE_TO_FIVE(
        "1-5",
        FoRanges(2, 5),
        FrValues(listOf(2, 3, 4, 5))
    ),
    ONE_TO_EIGHT(
        "1-8",
        FoRanges(2, 8),
        FrValues(listOf(2, 3, 5, 7))
    ),
    ONE_TO_TEN(
        "1-10",
        FoRanges(3, 10),
        FrValues(listOf(3, 5, 7, 9))
    ),
    ONE_TO_FIFTEEN(
        "1-15",
        FoRanges(4, 15),
        FrValues(listOf(4, 7, 10, 13))
    ),
    ONE_TO_TWENTY(
        "1-20",
        FoRanges(5, 20),
        FrValues(listOf(6, 10, 14, 18))
    ),
    ONE_TO_TWENTY_THREE(
        "1-23",
        FoRanges(8, 23),
        FrValues(listOf(9, 13, 17, 21))
    ),
    ONE_TO_TWENTY_FIVE(
        "1-25",
        FoRanges(6, 25),
        FrValues(listOf(8, 13, 18, 23))
    ),
}

data class FoRanges(
    val min: Int,
    val max: Int
)

data class FrValues(
    val values: List<Int>
)

fun SubjectiveRanges.toRangesResponse(): SubjectiveRangeDto {
    return SubjectiveRangeDto(
        name = this.name,
        displayName = this.displayName,
        foDisplay = "${foRanges.min}-${foRanges.max}",
        frDisplay = frValues.values.joinToString(", ")
    )
}

data class SubjectiveRangeDto(
    val name: String,
    val displayName: String,
    val foDisplay: String,
    val frDisplay: String
)