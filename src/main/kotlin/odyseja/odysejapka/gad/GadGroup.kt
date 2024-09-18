package odyseja.odysejapka.gad

data class GadGroup(val problem: Int, val age: Int, val part: String) {
    fun getDirName(): String {
        val suffix = if (part == "") {
            ""
        } else {
            "_$part"
        }
        return "P${problem}G${age}$suffix"
    }
}
