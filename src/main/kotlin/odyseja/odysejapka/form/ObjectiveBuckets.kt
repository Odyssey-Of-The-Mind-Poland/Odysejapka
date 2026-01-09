package odyseja.odysejapka.form

enum class ObjectiveBuckets(
    val displayName: String,
    val buckets: List<Int>
) {
    ZERO_THREE(
        "0, 3",
        listOf(0, 3)
    ),
    ZERO_FIVE(
        "0, 5",
        listOf(0, 5)
    ),
    ZERO_FIVE_TEN_FIFTEEN(
        "0, 5, 10, 15",
        listOf(0, 5, 10, 15)
    ),
}

fun ObjectiveBuckets.toBucketsResponse(): ObjectiveBucketDto {
    return ObjectiveBucketDto(
        name = this.name,
        displayName = this.displayName,
        bucketsDisplay = buckets.joinToString(", ")
    )
}

data class ObjectiveBucketDto(
    val name: String,
    val displayName: String,
    val bucketsDisplay: String
)

