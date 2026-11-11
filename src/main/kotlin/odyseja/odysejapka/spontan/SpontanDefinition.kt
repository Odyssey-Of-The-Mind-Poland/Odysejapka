package odyseja.odysejapka.spontan

data class SpontanDefinition(
    val id: Long? = null,
    val name: String,
    val type: SpontanType,
    val multiplier: Double? = null,
    val fields: List<SpontanFieldEntry> = emptyList()
) {
    fun isManual(): Boolean {
        return type == SpontanType.MANUAL || type == SpontanType.VERBAL_MANUAL
    }
}

enum class SpontanType {
    VERBAL,
    MANUAL,
    VERBAL_MANUAL
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
    val trueValue: Double = 0.0,
    val trueResponse: String = "Tak",
    val falseResponse: String = "Nie"
)
