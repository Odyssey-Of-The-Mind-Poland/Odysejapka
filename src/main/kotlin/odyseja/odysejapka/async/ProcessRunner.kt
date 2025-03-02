package odyseja.odysejapka.async

import odyseja.odysejapka.Progress
import odyseja.odysejapka.Status

class ProcessRunner(private val runner: Runner) {

    private var job: Thread? = null

    fun start() {
        if (job?.isAlive == true) {
            throw RuntimeException("Runner is already working")
        }
        job = Thread {
            runner.run()
        }
        job?.start()
    }

    fun stop() {
        job?.stop()
    }

    fun getProgress(): Progress {

        if (job?.isAlive == false) {
            return Progress(0, Status.STOPPED, getLogs())
        }

        val progress = runner.getProgress()
        return if (progress != 100) {
            Progress(progress, Status.RUNNING, getLogs())
        } else {
            Progress(100, Status.STOPPED, getLogs())
        }
    }

    fun getLogs(): List<Log> {
        return runner.getLogs()
    }
}