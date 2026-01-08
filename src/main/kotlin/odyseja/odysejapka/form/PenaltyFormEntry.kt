package odyseja.odysejapka.form

data class PenaltyFormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val entries: List<PenaltyFormEntry> = emptyList()
) {
    init {
        when (type) {
            EntryType.PENALTY -> {
                // PENALTY entries only have name, no scoring data
            }
            else -> {
                throw IllegalArgumentException("PenaltyFormEntry only supports PENALTY type, got: $type")
            }
        }
    }

    enum class EntryType {
        PENALTY
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
            this.name = this@PenaltyFormEntry.name
            this.formCategory = category
            this.parent = parent
            this.orderIndex = orderIndex
            this.entryType = FormEntryEntity.EntryType.PENALTY
        }
        return entity
    }
}

