package odyseja.odysejapka.spontan

data class SpontanDefinition(
    val id: Long? = null,
    val name: String,
    val type: SpontanType,
    val multiplier: Double? = null,
    val fields: List<SpontanFieldEntry> = emptyList()
)

enum class SpontanType {
    VERBAL,
    MANUAL
}

enum class SpontanFieldType {
    MULTIPLIER,
    EXPRESSION,
    BOOLEAN
}

data class SpontanFieldEntry(
    val id: Long? = null,
    val name: String,
    val multiplier: Double = 1.0,
    val fieldType: SpontanFieldType = SpontanFieldType.MULTIPLIER,
    val expression: String? = null,
    val trueValue: Double = 0.0
)
