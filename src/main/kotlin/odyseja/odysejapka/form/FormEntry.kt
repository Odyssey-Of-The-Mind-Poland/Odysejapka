package odyseja.odysejapka.form

data class FormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val punctuation: PunctuationData? = null,
    val punctuationGroup: PunctuationGroupData? = null,
    val entries: List<FormEntry> = emptyList()
) {
    init {
        when (type) {
            EntryType.PUNCTUATION -> {
                require(punctuation != null) { "Punctuation data is required for PUNCTUATION type" }
                require(punctuationGroup == null) { "PunctuationGroup data must be null for PUNCTUATION type" }
            }
            EntryType.SECTION -> {
                require(punctuation == null) { "Punctuation data must be null for SECTION type" }
                require(punctuationGroup == null) { "PunctuationGroup data must be null for SECTION type" }
            }
            EntryType.PUNCTUATION_GROUP -> {
                require(punctuationGroup != null) { "PunctuationGroup data is required for PUNCTUATION_GROUP type" }
                require(punctuation == null) { "Punctuation data must be null for PUNCTUATION_GROUP type" }
            }
        }
    }

    enum class EntryType {
        PUNCTUATION, SECTION, PUNCTUATION_GROUP
    }

    data class PunctuationData(
        val punctuationType: PunctuationType,
        val pointsMin: Int,
        val pointsMax: Int,
        val judges: JudgeType,
        val noElement: Boolean
    )

    data class PunctuationGroupData(
        val pointsMin: Int,
        val pointsMax: Int
    )

    enum class PunctuationType {
        SUBJECTIVE, OBJECTIVE
    }

    enum class JudgeType {
        A, B, A_PLUS_B
    }
}

enum class CalcType {
    SUM, AVERAGE
}
