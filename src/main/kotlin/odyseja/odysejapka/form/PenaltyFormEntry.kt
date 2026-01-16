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
}

