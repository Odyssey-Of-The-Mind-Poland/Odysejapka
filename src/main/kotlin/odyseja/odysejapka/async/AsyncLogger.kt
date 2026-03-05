package odyseja.odysejapka.async

import java.time.OffsetDateTime

class AsyncLogger {

    private val logs = ArrayList<Log>()

    fun log(msg: String) {
        println(msg)
        logs.add(Log(OffsetDateTime.now(), msg, LogLevel.INFO))
    }

    fun error(msg: String) {
        System.err.println(msg)
        logs.add(Log(OffsetDateTime.now(), msg, LogLevel.ERROR))
    }

    fun getLogs(): List<Log> {
        return logs
    }
}