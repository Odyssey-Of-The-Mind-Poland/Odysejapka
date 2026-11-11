package odyseja.odysejapka.spontan

data class TestFormulaRequest(val formula: String, val value: Double)
data class TestFormulaResult(val success: Boolean, val result: Double?)
