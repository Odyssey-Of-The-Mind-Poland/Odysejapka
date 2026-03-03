package odyseja.odysejapka.spontan

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpontanScoreCalculatorTest {

    private val calculator = SpontanScoreCalculator()
    private val offset = Offset.offset(0.001)

    private val verbalDefinition = SpontanDefinition(
        name = "Verbal",
        type = SpontanType.VERBAL
    )

    private fun manualDefinition(vararg fields: Pair<String, Double>) = SpontanDefinition(
        name = "Manual",
        type = SpontanType.MANUAL,
        fields = fields.map { SpontanFieldEntry(name = it.first, multiplier = it.second) }
    )

    private fun expressionField(name: String, expression: String) = SpontanFieldEntry(
        name = name,
        fieldType = SpontanFieldType.EXPRESSION,
        expression = expression
    )

    private fun booleanField(name: String, trueValue: Double) = SpontanFieldEntry(
        name = name,
        fieldType = SpontanFieldType.BOOLEAN,
        trueValue = trueValue
    )

    private fun manualDefinitionWithFields(vararg fields: SpontanFieldEntry) = SpontanDefinition(
        name = "Manual",
        type = SpontanType.MANUAL,
        fields = fields.toList()
    )

    private fun manual(field: String, value: Double) =
        ManualScoreEntry(field = field, value = value)

    @Nested
    inner class EdgeCases {

        @Test
        fun `should return 0 for empty results`() {
            assertThat(calculator.calculateScore(verbalDefinition, SpontanResults(), 3)).isEqualTo(0.0)
        }

        @Test
        fun `should return 0 for zero judges`() {
            val results = SpontanResults(verbalEntries = listOf(VerbalJudgeEntry(judge = 1, creative = 5.0)))
            assertThat(calculator.calculateScore(verbalDefinition, results, 0)).isEqualTo(0.0)
        }
    }

    @Nested
    inner class VerbalScore {

        @Test
        fun `should calculate score for single judge`() {
            val results = SpontanResults(verbalEntries = listOf(
                VerbalJudgeEntry(judge = 1, creative = 3.0, common = 2.0)
            ))
            // (3 * 5 + 2) / 1 = 17
            assertThat(calculator.calculateScore(verbalDefinition, results, 1)).isCloseTo(17.0, offset)
        }

        @Test
        fun `should average across judges`() {
            val results = SpontanResults(verbalEntries = listOf(
                VerbalJudgeEntry(judge = 1, creative = 4.0, common = 1.0),
                VerbalJudgeEntry(judge = 2, creative = 2.0, common = 3.0)
            ))
            // judge1: 4*5 + 1 = 21, judge2: 2*5 + 3 = 13, avg = 34/2 = 17
            assertThat(calculator.calculateScore(verbalDefinition, results, 2)).isCloseTo(17.0, offset)
        }

        @Test
        fun `should divide by judgeCount not by number of judges with entries`() {
            val results = SpontanResults(verbalEntries = listOf(
                VerbalJudgeEntry(judge = 1, creative = 6.0)
            ))
            // judge1: 6*5 + 0 = 30, divided by judgeCount=3 -> 10
            assertThat(calculator.calculateScore(verbalDefinition, results, 3)).isCloseTo(10.0, offset)
        }

        @Test
        fun `should treat missing fields as 0`() {
            val results = SpontanResults(verbalEntries = listOf(
                VerbalJudgeEntry(judge = 1, creative = 5.0)
            ))
            // 5*5 + 0 = 25 / 1 = 25
            assertThat(calculator.calculateScore(verbalDefinition, results, 1)).isCloseTo(25.0, offset)
        }

        @Test
        fun `should handle only common answers`() {
            val results = SpontanResults(verbalEntries = listOf(
                VerbalJudgeEntry(judge = 1, common = 10.0)
            ))
            // 0*5 + 10 = 10 / 1 = 10
            assertThat(calculator.calculateScore(verbalDefinition, results, 1)).isCloseTo(10.0, offset)
        }

        @Test
        fun `should handle three judges`() {
            val results = SpontanResults(verbalEntries = listOf(
                VerbalJudgeEntry(judge = 1, creative = 2.0, common = 1.0),
                VerbalJudgeEntry(judge = 2, creative = 3.0, common = 2.0),
                VerbalJudgeEntry(judge = 3, creative = 4.0, common = 3.0)
            ))
            // j1: 2*5+1=11, j2: 3*5+2=17, j3: 4*5+3=23, total=51/3=17
            assertThat(calculator.calculateScore(verbalDefinition, results, 3)).isCloseTo(17.0, offset)
        }
    }

    @Nested
    inner class ManualScore {

        @Test
        fun `should calculate judge score only when no fields defined`() {
            val definition = manualDefinition()
            val results = SpontanResults(manualJudgeEntries = listOf(
                ManualJudgeEntry(judge = 1, creativity = 8.0, teamwork = 7.0)
            ))
            // (8 + 7) / 1 = 15
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(15.0, offset)
        }

        @Test
        fun `should average judge scores across judges`() {
            val definition = manualDefinition()
            val results = SpontanResults(manualJudgeEntries = listOf(
                ManualJudgeEntry(judge = 1, creativity = 8.0, teamwork = 6.0),
                ManualJudgeEntry(judge = 2, creativity = 4.0, teamwork = 2.0)
            ))
            // j1: 8+6=14, j2: 4+2=6, total=20/2=10
            assertThat(calculator.calculateScore(definition, results, 2)).isCloseTo(10.0, offset)
        }

        @Test
        fun `should add field scores with multipliers`() {
            val definition = manualDefinition("Structure" to 2.0, "Costume" to 1.5)
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 5.0, teamwork = 5.0)
                ),
                manualEntries = listOf(
                    manual("Structure", 10.0),
                    manual("Costume", 4.0)
                )
            )
            // judgeScore: (5+5)/1 = 10
            // fieldScore: 10*2.0 + 4*1.5 = 20 + 6 = 26
            // total: 10 + 26 = 36
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(36.0, offset)
        }

        @Test
        fun `should ignore entries for unknown fields`() {
            val definition = manualDefinition("Structure" to 2.0)
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 5.0, teamwork = 5.0)
                ),
                manualEntries = listOf(
                    manual("Structure", 10.0),
                    manual("UnknownField", 99.0)
                )
            )
            // judgeScore: (5+5)/1 = 10, fieldScore: 10*2.0 = 20, total: 30
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(30.0, offset)
        }

        @Test
        fun `should handle missing judge fields as 0`() {
            val definition = manualDefinition("Structure" to 1.0)
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 7.0)
                ),
                manualEntries = listOf(
                    manual("Structure", 5.0)
                )
            )
            // judgeScore: (7+0)/1 = 7, fieldScore: 5*1.0 = 5, total: 12
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(12.0, offset)
        }

        @Test
        fun `should combine multiple judges with fields`() {
            val definition = manualDefinition("Weight" to 3.0)
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 6.0, teamwork = 4.0),
                    ManualJudgeEntry(judge = 2, creativity = 8.0, teamwork = 2.0)
                ),
                manualEntries = listOf(
                    manual("Weight", 5.0)
                )
            )
            // j1: 6+4=10, j2: 8+2=10, judgeScore: 20/2=10
            // fieldScore: 5*3.0 = 15
            // total: 10 + 15 = 25
            assertThat(calculator.calculateScore(definition, results, 2)).isCloseTo(25.0, offset)
        }

        @Test
        fun `should handle multiple field entries`() {
            val definition = manualDefinition("A" to 1.0, "B" to 2.0, "C" to 0.5)
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(
                    manual("A", 10.0),
                    manual("B", 5.0),
                    manual("C", 8.0)
                )
            )
            // judgeScore: 0/1 = 0
            // fieldScore: 10*1.0 + 5*2.0 + 8*0.5 = 10 + 10 + 4 = 24
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(24.0, offset)
        }

        @Test
        fun `should handle only manual entries with no judge entries`() {
            val definition = manualDefinition("Structure" to 2.0)
            val results = SpontanResults(
                manualEntries = listOf(manual("Structure", 10.0))
            )
            // judgeScore: 0/1 = 0, fieldScore: 10*2.0 = 20, total: 20
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(20.0, offset)
        }
    }

    @Nested
    inner class ExpressionFieldScore {

        @Test
        fun `should evaluate FLOOR expression`() {
            val definition = manualDefinitionWithFields(expressionField("Score", "FLOOR(v/3)*5"))
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(manual("Score", 9.0))
            )
            // FLOOR(9/3)*5 = 3*5 = 15
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(15.0, offset)
        }

        @Test
        fun `should evaluate simple multiplication expression`() {
            val definition = manualDefinitionWithFields(expressionField("Score", "v*1.5"))
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(manual("Score", 4.0))
            )
            // 4 * 1.5 = 6
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(6.0, offset)
        }

        @Test
        fun `should mix MULTIPLIER and EXPRESSION fields`() {
            val definition = manualDefinitionWithFields(
                SpontanFieldEntry(name = "Weight", multiplier = 2.0),
                expressionField("Bonus", "FLOOR(v/3)*5")
            )
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(
                    manual("Weight", 10.0),
                    manual("Bonus", 9.0)
                )
            )
            // Weight: 10*2.0 = 20, Bonus: FLOOR(9/3)*5 = 15, total: 35
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(35.0, offset)
        }

        @Test
        fun `should evaluate MIN capping expression`() {
            val definition = manualDefinitionWithFields(expressionField("Capped", "MIN(v, 100)"))
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(manual("Capped", 150.0))
            )
            // MIN(150, 100) = 100
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(100.0, offset)
        }
    }

    @Nested
    inner class BooleanFieldScore {

        @Test
        fun `should add trueValue when boolean field is true`() {
            val definition = manualDefinitionWithFields(booleanField("Bonus", 10.0))
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(manual("Bonus", 1.0))
            )
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(10.0, offset)
        }

        @Test
        fun `should add 0 when boolean field is false`() {
            val definition = manualDefinitionWithFields(booleanField("Bonus", 10.0))
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(manual("Bonus", 0.0))
            )
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(0.0, offset)
        }

        @Test
        fun `should mix BOOLEAN with MULTIPLIER fields`() {
            val definition = manualDefinitionWithFields(
                SpontanFieldEntry(name = "Weight", multiplier = 2.0),
                booleanField("Completed", 15.0)
            )
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(
                    manual("Weight", 5.0),
                    manual("Completed", 1.0)
                )
            )
            // Weight: 5*2.0 = 10, Completed: true -> 15, total: 25
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(25.0, offset)
        }

        @Test
        fun `should mix BOOLEAN with EXPRESSION fields`() {
            val definition = manualDefinitionWithFields(
                expressionField("Score", "v*3"),
                booleanField("Penalty", -5.0)
            )
            val results = SpontanResults(
                manualJudgeEntries = listOf(
                    ManualJudgeEntry(judge = 1, creativity = 0.0, teamwork = 0.0)
                ),
                manualEntries = listOf(
                    manual("Score", 4.0),
                    manual("Penalty", 1.0)
                )
            )
            // Score: 4*3 = 12, Penalty: true -> -5, total: 7
            assertThat(calculator.calculateScore(definition, results, 1)).isCloseTo(7.0, offset)
        }
    }

    @Nested
    inner class EvaluateExpression {

        @Test
        fun `should evaluate simple arithmetic`() {
            assertThat(calculator.evaluateExpression("v*2", 5.0)).isCloseTo(10.0, offset)
        }

        @Test
        fun `should evaluate FLOOR`() {
            assertThat(calculator.evaluateExpression("FLOOR(v/3)", 10.0)).isCloseTo(3.0, offset)
        }

        @Test
        fun `should evaluate CEILING`() {
            assertThat(calculator.evaluateExpression("CEILING(v/3)", 10.0)).isCloseTo(4.0, offset)
        }

        @Test
        fun `should evaluate nested functions`() {
            assertThat(calculator.evaluateExpression("FLOOR(v/3)*5", 9.0)).isCloseTo(15.0, offset)
        }

        @Test
        fun `should evaluate MIN`() {
            assertThat(calculator.evaluateExpression("MIN(v, 100)", 150.0)).isCloseTo(100.0, offset)
        }

        @Test
        fun `should evaluate MAX`() {
            assertThat(calculator.evaluateExpression("MAX(v, 10)", 5.0)).isCloseTo(10.0, offset)
        }

        @Test
        fun `should evaluate ABS`() {
            assertThat(calculator.evaluateExpression("ABS(v)", -7.0)).isCloseTo(7.0, offset)
        }

        @Test
        fun `should evaluate SQRT`() {
            assertThat(calculator.evaluateExpression("SQRT(v)", 16.0)).isCloseTo(4.0, offset)
        }
    }
}
