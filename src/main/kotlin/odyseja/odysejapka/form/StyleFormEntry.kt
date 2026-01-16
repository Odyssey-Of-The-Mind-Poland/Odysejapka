package odyseja.odysejapka.form

data class StyleFormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val styleType: StyleType = StyleType.PREDEFINED,
    val entries: List<StyleFormEntry> = emptyList(),
    val sortIndex: Int = 0
) {
    init {
        when (type) {
            EntryType.STYLE -> {
                // STYLE entries only have name, no scoring data
            }
            else -> {
                throw IllegalArgumentException("StyleFormEntry only supports STYLE type, got: $type")
            }
        }
    }

    enum class EntryType {
        STYLE
    }

    enum class StyleType {
        PREDEFINED, FREE_TEAM_CHOICE
    }
}

