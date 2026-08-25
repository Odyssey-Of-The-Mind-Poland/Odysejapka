package odyseja.odysejapka

private val FOREIGN_COUNTRIES = listOf("spain", "china", "slovakia")

fun isForeigner(city: String, teamName: String): Boolean {
    val cityLower = city.lowercase()
    val teamNameLower = teamName.lowercase()
    return FOREIGN_COUNTRIES.any { country ->
        cityLower.contains(country) || teamNameLower.contains(country)
    }
}
