package odyseja.odysejapka.form

data class WeightHeldFormEntry(
    val id: Long?,
    val sortIndex: Int = 0
) {
    companion object {
        const val PROBLEM_4_ENTRY_ID = -1L
        val ALLOWED_WEIGHTS = listOf(2.5, 5.0, 10.0, 15.0, 20.0, 25.0)

        fun forProblem4() = WeightHeldFormEntry(id = PROBLEM_4_ENTRY_ID)
    }
}
