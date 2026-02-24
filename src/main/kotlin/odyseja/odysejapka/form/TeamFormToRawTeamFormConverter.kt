package odyseja.odysejapka.form

data class RawTeamFormEntry(
    val name: String,
    val averageScore: Double?,
    val noElement: Boolean = false,
    val isSectionHeader: Boolean = false
)

data class RawTeamForm(
    val teamName: String,
    val cityName: String,
    val problem: Int,
    val age: Int,
    val performanceAt: String = "",
    val performanceTime: String = "",
    val dtEntries: List<RawTeamFormEntry>,
    val dtSum: Double,
    val styleEntries: List<RawTeamFormEntry>,
    val styleSum: Double,
    val penaltyEntries: List<RawTeamFormEntry>,
    val penaltySum: Double,
    val totalSum: Double
)

object TeamFormToRawTeamFormConverter {

    fun convert(teamForm: TeamForm): RawTeamForm {
        val dtEntries = flattenDtEntries(teamForm.dtEntries)
        val styleEntries = teamForm.styleEntries.map { entry ->
            RawTeamFormEntry(
                name = entry.entry.name,
                averageScore = calculateAverage(entry.results),
                noElement = false
            )
        }
        val penaltyEntries = teamForm.penaltyEntries.map { entry ->
            RawTeamFormEntry(
                name = entry.entry.name,
                averageScore = entry.result?.toDouble(),
                noElement = false
            )
        }

        val dtSum = dtEntries.sumOf { it.averageScore ?: 0.0 }
        val styleSum = styleEntries.sumOf { it.averageScore ?: 0.0 }
        val penaltySum = penaltyEntries.sumOf { it.averageScore ?: 0.0 }
        val totalSum = dtSum + styleSum + penaltySum

        return RawTeamForm(
            teamName = teamForm.teamName,
            cityName = teamForm.cityName,
            problem = teamForm.problem,
            age = teamForm.age,
            performanceAt = teamForm.performanceAt,
            performanceTime = teamForm.performanceTime,
            dtEntries = dtEntries,
            dtSum = dtSum,
            styleEntries = styleEntries,
            styleSum = styleSum,
            penaltyEntries = penaltyEntries,
            penaltySum = penaltySum,
            totalSum = totalSum
        )
    }

    /**
     * Flattens DT entries with group handling:
     * - SCORING_GROUP: collapsed to one row (group name + sum of subentries)
     * - SECTION: section name as header row, then nested entries as a., b., c., etc.
     * - SCORING (leaf): single row with score
     */
    private fun flattenDtEntries(entries: List<TeamForm.DtTeamFormEntry>): List<RawTeamFormEntry> {
        val result = mutableListOf<RawTeamFormEntry>()

        fun processEntry(entry: TeamForm.DtTeamFormEntry, nestingLevel: Int, index: Int, isInsideScoringGroup: Boolean): Double {
            val isScoringGroup = entry.entry.type == LongTermFormEntry.EntryType.SCORING_GROUP
            val isSection = entry.entry.type == LongTermFormEntry.EntryType.SECTION
            val hasNested = entry.nestedEntries.isNotEmpty()

            return when {
                isScoringGroup && hasNested -> {
                    val nestedSum = entry.nestedEntries.mapIndexed { i, nested ->
                        processEntry(nested, nestingLevel + 1, i, isInsideScoringGroup = true)
                    }.sum()
                    if (!isInsideScoringGroup) {
                        val prefix = when (nestingLevel) {
                            0 -> "${entry.entry.sortIndex}."
                            1 -> "${('a'.code + index).toChar()}."
                            else -> ""
                        }
                        val name = if (prefix.isNotEmpty()) "$prefix ${entry.entry.name}" else entry.entry.name
                        result.add(RawTeamFormEntry(name = name, averageScore = nestedSum, noElement = false))
                    }
                    nestedSum
                }
                isSection && hasNested -> {
                    if (!isInsideScoringGroup) {
                        val prefix = when (nestingLevel) {
                            0 -> "${entry.entry.sortIndex}."
                            1 -> "${('a'.code + index).toChar()}."
                            else -> ""
                        }
                        val name = if (prefix.isNotEmpty()) "$prefix ${entry.entry.name}" else entry.entry.name
                        result.add(RawTeamFormEntry(name = name, averageScore = null, noElement = false, isSectionHeader = true))
                    }
                    entry.nestedEntries.mapIndexed { i, nested ->
                        processEntry(nested, nestingLevel + 1, i, isInsideScoringGroup)
                    }.sum()
                }
                else -> {
                    val score = calculateAverage(entry.results) ?: 0.0
                    if (!isInsideScoringGroup) {
                        val prefix = when (nestingLevel) {
                            0 -> "${entry.entry.sortIndex}."
                            1 -> "${('a'.code + index).toChar()}."
                            else -> ""
                        }
                        val name = if (prefix.isNotEmpty()) "$prefix ${entry.entry.name}" else entry.entry.name
                        result.add(
                            RawTeamFormEntry(
                                name = name,
                                averageScore = score,
                                noElement = entry.noElement
                            )
                        )
                    }
                    score
                }
            }
        }

        entries.forEachIndexed { i, entry ->
            processEntry(entry, 0, i, isInsideScoringGroup = false)
        }
        return result
    }

    private fun calculateAverage(results: Map<JudgeType, Map<Int, Long?>>): Double? {
        val allScores = results.values.flatMap { judgeScores ->
            judgeScores.values.filterNotNull()
        }
        
        if (allScores.isEmpty()) return null
        
        return allScores.map { it.toDouble() }.average()
    }
}

