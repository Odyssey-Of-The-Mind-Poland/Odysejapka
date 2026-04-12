package odyseja.odysejapka.breaking.change

data class BreakingChange(
    val version: String
) {
    fun validate() {
        val versionAndBuild = version.split("+")
        require(versionAndBuild.size <= 2)
            {"Wersja nie może mieć więcej niż 1 build number"}

        val versionParts = versionAndBuild.first().split(".")
        val buildNumber = if (versionAndBuild.size == 2) {
            versionAndBuild.last()
        } else null

        val versionElements = versionParts + listOfNotNull(buildNumber)
        require(versionElements.all { it.toIntOrNull() != null })
            { "Wersja musi być w formacie W.X.Y+Z, gdzie W, X, Y i Z są liczbami (Int)" }
    }
}
