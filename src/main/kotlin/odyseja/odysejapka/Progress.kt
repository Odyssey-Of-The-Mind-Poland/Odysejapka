package odyseja.odysejapka

import odyseja.odysejapka.async.Log

data class Progress(val progress: Int, val status: Status, val logs: List<Log>)
