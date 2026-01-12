package odyseja.odysejapka.form

data class SetJudgesRequest(
    val smallJudgesTeam: List<Int>,
    val bigJudgesTeam: List<Int>
)
