package odyseja.odysejapka.async

interface Runner {

    fun run()

    fun getProgress(): Int

    fun getLogs(): List<Log>
}