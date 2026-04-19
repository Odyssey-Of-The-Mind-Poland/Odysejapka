package odyseja.odysejapka.breaking.change

data class BreakingChange(
    val version: String
) {
    fun validate() {
        require(version.isNotBlank()) { "Wersja nie może być pusta" }
        val versionParts = version.split(".")
        require(versionParts.all { it.toIntOrNull() != null })
            { "Wersja musi być w formacie X.Y.Z, gdzie X, Y i Z są liczbami (Int)" }
    }
}
