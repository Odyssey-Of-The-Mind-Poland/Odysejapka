package odyseja.odysejapka.form

import org.springframework.stereotype.Service

@Service
class FormValidationService {

    companion object {
        const val PERFORMANCE_AT_ENTRY_ID = -1L
        const val PERFORMANCE_TIME_ENTRY_ID = -2L
    }

    fun validateTeamForm(teamForm: TeamForm): List<ValidationFailure> {
        if (!hasAnyValues(teamForm)) {
            return emptyList()
        }
        val failures = mutableListOf<ValidationFailure>()
        failures.addAll(validatePerformanceFields(teamForm))
        failures.addAll(validateDtEntries(teamForm.dtEntries, teamForm.judgeCount))
        failures.addAll(validateStyleEntriesAllJudges(teamForm.styleEntries, teamForm.judgeCount))
        failures.addAll(validatePenaltyEntries(teamForm.penaltyEntries))
        failures.addAll(validateWeightHeldEntries(teamForm.weightHeldEntries))
        return failures
    }

    private fun hasAnyValues(teamForm: TeamForm): Boolean {
        if (teamForm.performanceAt.isNotBlank() || teamForm.performanceTime.isNotBlank()) return true
        if (hasAnyDtValues(teamForm.dtEntries)) return true
        if (teamForm.styleEntries.any { entry -> entry.results.values.any { judgeMap -> judgeMap.values.any { it != null } } }) return true
        if (teamForm.penaltyEntries.any { it.result != null || it.zeroBalsa == true }) return true
        if (teamForm.weightHeldEntries.any { it.weights.isNotEmpty() }) return true
        return false
    }

    private fun hasAnyDtValues(entries: List<TeamForm.DtTeamFormEntry>): Boolean {
        for (entry in entries) {
            if (entry.noElement) return true
            if (entry.results.values.any { judgeMap -> judgeMap.values.any { it != null } }) return true
            if (hasAnyDtValues(entry.nestedEntries)) return true
        }
        return false
    }

    private fun validatePerformanceFields(teamForm: TeamForm): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()
        if (teamForm.performanceAt.isBlank()) {
            failures.add(
                ValidationFailure(
                    entryId = PERFORMANCE_AT_ENTRY_ID,
                    rule = "performance-at-required",
                    message = "Godzina występu jest wymagana"
                )
            )
        }
        if (teamForm.performanceTime.isBlank()) {
            failures.add(
                ValidationFailure(
                    entryId = PERFORMANCE_TIME_ENTRY_ID,
                    rule = "performance-time-required",
                    message = "Czas trwania występu jest wymagany"
                )
            )
        }
        return failures
    }

    private fun validateDtEntries(entries: List<TeamForm.DtTeamFormEntry>, judgeCount: Int): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()

        for (dtEntry in entries) {
            failures.addAll(validateSingleDtEntry(dtEntry, judgeCount))

            if (dtEntry.nestedEntries.isNotEmpty()) {
                failures.addAll(validateDtEntries(dtEntry.nestedEntries, judgeCount))
            }
        }

        return failures
    }

    private fun validateSingleDtEntry(dtEntry: TeamForm.DtTeamFormEntry, judgeCount: Int): List<ValidationFailure> {
        val entryId = dtEntry.entry.id ?: return emptyList()
        val scoring = dtEntry.entry.scoring ?: return emptyList()
        val failures = mutableListOf<ValidationFailure>()

        if (dtEntry.noElement) {
            validateNoElementCommentRequired(dtEntry, entryId)?.let { failures.add(it) }
            return failures
        }

        validateAllJudgesFilled(dtEntry, entryId, judgeCount)?.let { failures.add(it) }

        if (scoring.scoringType == LongTermFormEntry.ScoringType.OBJECTIVE) {
            validateObjectiveSameScore(dtEntry, entryId)?.let { failures.add(it) }
        }

        return failures
    }

    /**
     * Rule: all-judges-required
     * All judges must have a value for each scoring entry.
     */
    private fun validateAllJudgesFilled(
        dtEntry: TeamForm.DtTeamFormEntry,
        entryId: Long,
        judgeCount: Int
    ): ValidationFailure? {
        val scoring = dtEntry.entry.scoring ?: return null

        val enabledTypes = when (scoring.judges) {
            LongTermFormEntry.JudgesType.A -> listOf(JudgeType.DT_A)
            LongTermFormEntry.JudgesType.B -> listOf(JudgeType.DT_B)
            LongTermFormEntry.JudgesType.A_PLUS_B -> listOf(JudgeType.DT_A, JudgeType.DT_B)
        }

        for (judgeType in enabledTypes) {
            val judgeMap = dtEntry.results[judgeType] ?: continue
            for (judgeIndex in 1..judgeCount) {
                judgeMap[judgeIndex] ?: return ValidationFailure(
                    entryId = entryId,
                    rule = "all-judges-required",
                    message = "Wszyscy sędziowie muszą przyznać punkty"
                )
            }
        }
        return null
    }

    /**
     * Rule: all-style-judges-required
     * All style judges must have a value for each style entry.
     */
    private fun validateStyleEntriesAllJudges(
        entries: List<TeamForm.StyleTeamFormEntry>,
        judgeCount: Int
    ): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()
        for (entry in entries) {
            val entryId = entry.entry.id ?: continue
            val styleMap = entry.results[JudgeType.STYLE] ?: continue
            for (judgeIndex in 1..judgeCount) {
                val value = styleMap[judgeIndex]
                if (value == null) {
                    failures.add(
                        ValidationFailure(
                            entryId = entryId,
                            rule = "all-judges-required",
                            message = "Wszyscy sędziowie muszą przyznać punkty"
                        )
                    )
                    break
                }
            }
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

    private fun validateWeightHeldEntries(entries: List<TeamForm.WeightHeldTeamFormEntry>): List<ValidationFailure> {
        val failures = mutableListOf<ValidationFailure>()

        for (weightHeldEntry in entries) {
            val entryId = weightHeldEntry.entry.id ?: continue
            val weights = weightHeldEntry.weights

            if (weights.isEmpty()) continue

            if (weights.first() != 2.5) {
                failures.add(
                    ValidationFailure(
                        entryId = entryId,
                        rule = "weight-held-first-must-be-2.5",
                        message = "Pierwszy ciężar musi wynosić 2,5 kg"
                    )
                )
            }

            for (weight in weights) {
                if (weight !in WeightHeldFormEntry.ALLOWED_WEIGHTS) {
                    failures.add(
                        ValidationFailure(
                            entryId = entryId,
                            rule = "weight-held-invalid-value",
                            message = "Niedozwolona wartość ciężaru: $weight"
                        )
                    )
                    break
                }
            }
        }

        return failures
    }
}
