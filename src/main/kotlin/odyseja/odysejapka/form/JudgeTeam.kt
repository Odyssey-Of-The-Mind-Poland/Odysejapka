package odyseja.odysejapka.form

/**
 * Ta klasa reprezentuje jeden zespół sędziowski – czyli kombinację liczby sędziów A, B i Stylu w danym problemie na danym konkursie.
 */

data class JudgeTeam(
    val problem: Int,
    val cityId: Int,
    val judgeCount: JudgeCount
)