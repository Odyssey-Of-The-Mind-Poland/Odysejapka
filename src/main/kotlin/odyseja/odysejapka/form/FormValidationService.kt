package odyseja.odysejapka.form

import org.springframework.stereotype.Service

@Service
class FormValidationService {

    fun validateTeamForm(teamForm: TeamForm): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()
        failures.addAll(validateDtEntries(teamForm.dtEntries))
        failures.addAll(validatePenaltyEntries(teamForm.penaltyEntries))
        return failures
    }

    private fun validateDtEntries(entries: List<TeamForm.DtTeamFormEntry>): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()

        for (dtEntry in entries) {
            failures.addAll(validateSingleDtEntry(dtEntry))

            if (dtEntry.nestedEntries.isNotEmpty()) {
                failures.addAll(validateDtEntries(dtEntry.nestedEntries))
            }
        }

        return failures
    }

    private fun validateSingleDtEntry(dtEntry: TeamForm.DtTeamFormEntry): List<ValidationFailure> {
        val entryId = dtEntry.entry.id ?: return emptyList()
        val scoring = dtEntry.entry.scoring ?: return emptyList()
        val failures = mutableListOf<ValidationFailure>()

        if (dtEntry.noElement) {
            validateNoElementCommentRequired(dtEntry, entryId)?.let { failures.add(it) }
            return failures
        }

        if (scoring.scoringType == LongTermFormEntry.ScoringType.OBJECTIVE) {
            validateObjectiveSameScore(dtEntry, entryId)?.let { failures.add(it) }
        }

        return failures
    }

    /**
     * Rule: no-element-comment-required
     * When "Brak elementu" is checked, a comment is required.
     */
    private fun validateNoElementCommentRequired(
        dtEntry: TeamForm.DtTeamFormEntry,
        entryId: Long
    ): ValidationFailure? {
        if (!dtEntry.noElement) return null

        val hasComment = !dtEntry.noElementComment.isNullOrBlank()
        if (hasComment) return null

        return ValidationFailure(
            entryId = entryId,
            rule = "no-element-comment-required",
            message = "Komentarz jest wymagany gdy zaznaczono brak elementu"
        )
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

    private fun validatePenaltyEntries(entries: List<TeamForm.PenaltyTeamFormEntry>): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()

        for (penaltyEntry in entries) {
            validatePenaltyCommentRequired(penaltyEntry)?.let { failures.add(it) }
        }

        return failures
    }

    /**
     * Rule: penalty-comment-required
     * When a penalty value is set (non-zero), a comment is required.
     */
    private fun validatePenaltyCommentRequired(
        penaltyEntry: TeamForm.PenaltyTeamFormEntry
    ): ValidationFailure? {
        val entryId = penaltyEntry.entry.id ?: return null

        val hasPenalty = when (penaltyEntry.entry.penaltyType) {
            PenaltyFormEntry.PenaltyType.ZERO_BALSA -> penaltyEntry.zeroBalsa == true
            else -> penaltyEntry.result != null && penaltyEntry.result != 0L
        }

        if (!hasPenalty) return null

        val hasComment = !penaltyEntry.comment.isNullOrBlank()
        if (hasComment) return null

        return ValidationFailure(
            entryId = entryId,
            rule = "penalty-comment-required",
            message = "Komentarz jest wymagany gdy kara jest naliczona"
        )
    }
}
