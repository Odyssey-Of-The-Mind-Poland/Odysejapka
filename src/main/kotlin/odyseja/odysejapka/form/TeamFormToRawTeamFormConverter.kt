package odyseja.odysejapka.form

data class RawTeamFormEntry(
    val name: String,
    val averageScore: Double?,
    val noElement: Boolean = false
)

data class RawTeamForm(
    val teamName: String,
    val cityName: String,
    val problem: Int,
    val age: Int,
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
            dtEntries = dtEntries,
            dtSum = dtSum,
            styleEntries = styleEntries,
            styleSum = styleSum,
            penaltyEntries = penaltyEntries,
            penaltySum = penaltySum,
            totalSum = totalSum
        )
    }

    private fun flattenDtEntries(entries: List<TeamForm.DtTeamFormEntry>): List<RawTeamFormEntry> {
        val result = mutableListOf<RawTeamFormEntry>()
        
        fun processEntry(entry: TeamForm.DtTeamFormEntry, indent: Int = 0) {
            val prefix = "  ".repeat(indent)
            result.add(
                RawTeamFormEntry(
                    name = "$prefix${entry.entry.name}",
                    averageScore = calculateAverage(entry.results),
                    noElement = entry.noElement
                )
            )
            
            entry.nestedEntries.forEach { nested ->
                processEntry(nested, indent + 1)
            }
        }
        
        entries.forEach { entry ->
            processEntry(entry)
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

