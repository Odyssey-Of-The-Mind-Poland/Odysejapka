package odyseja.odysejapka.form

data class FormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val calcType: CalcType? = null,
    val entries: List<FormEntry> = emptyList()
) {
    enum class EntryType {
        PUNCTUATION, SECTION, PUNCTUATION_GROUP
    }
}

enum class CalcType {
    SUM, AVERAGE
}
