package odyseja.odysejapka.form

import org.springframework.stereotype.Service

@Service
class FormValidationService {

    fun validateTeamForm(teamForm: TeamForm): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()
        failures.addAll(validateDtEntries(teamForm.dtEntries))
        return failures
    }

    private fun validateDtEntries(entries: List<TeamForm.DtTeamFormEntry>): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()

        for (dtEntry in entries) {
            validateSingleDtEntry(dtEntry)?.let { failures.add(it) }

            if (dtEntry.nestedEntries.isNotEmpty()) {
                failures.addAll(validateDtEntries(dtEntry.nestedEntries))
            }
        }

        return failures
    }

    private fun validateSingleDtEntry(dtEntry: TeamForm.DtTeamFormEntry): ValidationFailure? {
        val scoring = dtEntry.entry.scoring ?: return null
        val entryId = dtEntry.entry.id ?: return null

        if (dtEntry.noElement) return null

        if (scoring.scoringType == LongTermFormEntry.ScoringType.OBJECTIVE) {
            return validateObjectiveSameScore(dtEntry, entryId)
        }

        return null
    }

    /**
     * Rule: objective-same-score
     * All judges must assign the same score for OBJECTIVE entries.
     */
    private fun validateObjectiveSameScore(
        dtEntry: TeamForm.DtTeamFormEntry,
        entryId: Long
    ): ValidationFailure? {
        val scoring = dtEntry.entry.scoring ?: return null

        val enabledTypes = when (scoring.judges) {
            LongTermFormEntry.JudgesType.A -> listOf(JudgeType.DT_A)
            LongTermFormEntry.JudgesType.B -> listOf(JudgeType.DT_B)
            LongTermFormEntry.JudgesType.A_PLUS_B -> listOf(JudgeType.DT_A, JudgeType.DT_B)
        }

        val allValues = mutableListOf<Long>()

        for (judgeType in enabledTypes) {
            val judgeMap = dtEntry.results[judgeType] ?: continue
            for ((_, value) in judgeMap) {
                if (value != null) {
                    allValues.add(value)
                }
            }
        }

        if (allValues.size < 2) return null

        val allSame = allValues.all { it == allValues[0] }
        if (allSame) return null

        return ValidationFailure(
            entryId = entryId,
            rule = "objective-same-score",
            message = "Wszyscy sędziowie muszą przyznać tę samą liczbę punktów"
        )
    }
}
