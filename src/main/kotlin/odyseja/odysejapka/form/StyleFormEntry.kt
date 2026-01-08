package odyseja.odysejapka.form

data class StyleFormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val entries: List<StyleFormEntry> = emptyList()
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

    fun toEntity(
        problem: Int,
        category: FormEntryEntity.FormCategory,
        parent: FormEntryEntity?,
        orderIndex: Int,
        existing: FormEntryEntity? = null
    ): FormEntryEntity {
        val entity = existing ?: FormEntryEntity()
        entity.apply {
            this.problem = problem
            this.name = this@StyleFormEntry.name
            this.formCategory = category
            this.parent = parent
            this.orderIndex = orderIndex
            this.entryType = FormEntryEntity.EntryType.STYLE
        }
        return entity
    }
}

