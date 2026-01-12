package odyseja.odysejapka.form

data class PenaltyFormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val penaltyRange: RangeData? = null,
    val penaltyDiscrete: DiscreteData? = null,
    val penaltySingle: SingleData? = null,
    val entries: List<PenaltyFormEntry> = emptyList(),
    val sortIndex: Int = 0
) {
    init {
        val typesSet = listOfNotNull(penaltyRange, penaltyDiscrete, penaltySingle).size
        require(typesSet <= 1) { "Only one penalty type can be set at a time" }
    }

    enum class EntryType {
        PENALTY
    }

    data class RangeData(val min: Double, val max: Double) {
        init {
            require(min <= max) { "Range min ($min) must be <= max ($max)" }
        }
    }
    
    data class DiscreteData(val values: List<Double>) {
        init {
            require(values.isNotEmpty()) { "Discrete values list cannot be empty" }
        }
    }
    
    data class SingleData(val value: Double)

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
            this.orderIndex = this@PenaltyFormEntry.sortIndex
            this.entryType = FormEntryEntity.EntryType.PENALTY
            
            when {
                penaltyRange != null -> {
                    this.penaltyValueType = FormEntryEntity.PenaltyValueType.RANGE
                    this.penaltyMin = penaltyRange.min
                    this.penaltyMax = penaltyRange.max
                    this.penaltySingleValue = null
                    this.penaltyDiscreteValues = null
                }
                penaltyDiscrete != null -> {
                    this.penaltyValueType = FormEntryEntity.PenaltyValueType.DISCRETE
                    this.penaltyMin = null
                    this.penaltyMax = null
                    this.penaltySingleValue = null
                    this.penaltyDiscreteValues = penaltyDiscrete.values
                }
                penaltySingle != null -> {
                    this.penaltyValueType = FormEntryEntity.PenaltyValueType.SINGLE
                    this.penaltyMin = null
                    this.penaltyMax = null
                    this.penaltySingleValue = penaltySingle.value
                    this.penaltyDiscreteValues = null
                }
                else -> {
                    this.penaltyValueType = null
                    this.penaltyMin = null
                    this.penaltyMax = null
                    this.penaltySingleValue = null
                    this.penaltyDiscreteValues = null
                }
            }
        }
        return entity
    }
}

