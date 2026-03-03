package odyseja.odysejapka.spontan

import com.ezylang.evalex.Expression

class SpontanScoreCalculator {

    fun calculateScore(
        definition: SpontanDefinition,
        results: SpontanResults,
        judgeCount: Int
    ): Double {
        if (judgeCount == 0) return 0.0

        return when (definition.type) {
            SpontanType.VERBAL -> calculateVerbalScore(results.verbalEntries, judgeCount)
            SpontanType.MANUAL -> calculateManualScore(definition, results, judgeCount)
        }
    }

    private fun calculateVerbalScore(entries: List<VerbalJudgeEntry>, judgeCount: Int): Double {
        if (entries.isEmpty()) return 0.0
        var total = 0.0
        for (entry in entries) {
            total += entry.creative * 5 + entry.common
        }
        return total / judgeCount
    }

    private fun calculateManualScore(
        definition: SpontanDefinition,
        results: SpontanResults,
        judgeCount: Int
    ): Double {
        if (results.manualJudgeEntries.isEmpty() && results.manualEntries.isEmpty()) return 0.0

        var judgeTotal = 0.0
        for (entry in results.manualJudgeEntries) {
            judgeTotal += entry.creativity + entry.teamwork
        }
        val judgeScore = judgeTotal / judgeCount

        var fieldScore = 0.0
        val fieldDefs = definition.fields.associateBy { it.name }
        for (entry in results.manualEntries) {
            val fieldDef = fieldDefs[entry.field] ?: continue
            fieldScore += when (fieldDef.fieldType) {
                SpontanFieldType.MULTIPLIER -> entry.value * fieldDef.multiplier
                SpontanFieldType.EXPRESSION -> evaluateExpression(fieldDef.expression!!, entry.value)
                SpontanFieldType.BOOLEAN -> if (entry.value != 0.0) fieldDef.trueValue else 0.0
            }
        }

        return judgeScore + fieldScore
    }

    fun evaluateExpression(expression: String, value: Double): Double {
        val result = Expression(expression)
            .with("v", value)
            .evaluate()
        return result.numberValue.toDouble()
    }
}
