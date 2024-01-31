data class Team(val hour: String, val code: String, val teamName: String, val zspIndex: Int) {
    fun getFileName(): String {
        return "$`code`_$teamName"
    }

    fun getAge(): String {
        return code[3].toString()
    }

    fun getProblem(): String {
        return code[1].toString()
    }
}
