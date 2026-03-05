package odyseja.odysejapka.async

import java.time.OffsetDateTime

enum class LogLevel {
    INFO,
    ERROR
}

data class Log(val logTime: OffsetDateTime, val message: String, val level: LogLevel = LogLevel.INFO)
