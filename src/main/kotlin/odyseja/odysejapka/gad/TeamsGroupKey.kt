package odyseja.odysejapka.gad

data class TeamsGroupKey(val problem: Int, val age: Int, val league: String) {
    fun getDirName(): String {
        val suffix = if (league == "") {
            ""
        } else {
            "_$league"
        }
        return "P${problem}G${age}$suffix"
    }
}
