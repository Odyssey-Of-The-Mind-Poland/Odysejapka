package odyseja.odysejapka.form

data class PenaltyFormEntry(
    val id: Long?,
    val name: String,
    val type: EntryType,
    val penaltyType: PenaltyType? = null,
    val penaltyRange: RangeData? = null,
    val penaltyDiscrete: DiscreteData? = null,
    val penaltySingle: SingleData? = null,
    val entries: List<PenaltyFormEntry> = emptyList(),
    val sortIndex: Int = 0
) {
    init {
        when (type) {
            EntryType.PENALTY -> {
                val typesSet = listOfNotNull(penaltyRange, penaltyDiscrete, penaltySingle).size
                require(typesSet <= 1) { "Only one penalty type can be set at a time" }
                if (penaltyType != null) {
                    when (penaltyType) {
                        PenaltyType.RANGE -> require(penaltyRange != null) { "Range data must be set for RANGE type" }
                        PenaltyType.DISCRETE -> require(penaltyDiscrete != null) { "Discrete data must be set for DISCRETE type" }
                        PenaltyType.SINGLE -> require(penaltySingle != null) { "Single data must be set for SINGLE type" }
                        PenaltyType.ZERO_BALSA -> {
                            require(penaltyRange == null && penaltyDiscrete == null && penaltySingle == null) {
                                "No penalty data should be set for ZERO_BALSA type"
                            }
                        }
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("PenaltyFormEntry only supports PENALTY type, got: $type")
            }
        }
    }

    enum class EntryType {
        PENALTY
    }

    enum class PenaltyType {
        RANGE, DISCRETE, SINGLE, ZERO_BALSA
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
            
            val inferredPenaltyType = this@PenaltyFormEntry.penaltyType ?: when {
                penaltyRange != null -> PenaltyType.RANGE
                penaltyDiscrete != null -> PenaltyType.DISCRETE
                penaltySingle != null -> PenaltyType.SINGLE
                else -> null
            }
            
            when (inferredPenaltyType) {
                PenaltyType.RANGE -> {
                    this.penaltyValueType = FormEntryEntity.PenaltyValueType.RANGE
                    this.penaltyMin = penaltyRange?.min
                    this.penaltyMax = penaltyRange?.max
                    this.penaltySingleValue = null
                    this.penaltyDiscreteValues = null
                }
                PenaltyType.DISCRETE -> {
                    this.penaltyValueType = FormEntryEntity.PenaltyValueType.DISCRETE
                    this.penaltyMin = null
                    this.penaltyMax = null
                    this.penaltySingleValue = null
                    this.penaltyDiscreteValues = penaltyDiscrete?.values
                }
                PenaltyType.SINGLE -> {
                    this.penaltyValueType = FormEntryEntity.PenaltyValueType.SINGLE
                    this.penaltyMin = null
                    this.penaltyMax = null
                    this.penaltySingleValue = penaltySingle?.value
                    this.penaltyDiscreteValues = null
                }
                PenaltyType.ZERO_BALSA -> {
                    this.penaltyValueType = FormEntryEntity.PenaltyValueType.ZERO_BALSA
                    this.penaltyMin = null
                    this.penaltyMax = null
                    this.penaltySingleValue = null
                    this.penaltyDiscreteValues = null
                }
                null -> {
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

