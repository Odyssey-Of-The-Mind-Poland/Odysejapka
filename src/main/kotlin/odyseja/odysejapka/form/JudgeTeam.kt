package odyseja.odysejapka.form

import odyseja.odysejapka.city.CityEntity

data class JudgeTeam(
    val problem: Int,
    val city: CityEntity,
    val judgeCount: JudgeCount
)