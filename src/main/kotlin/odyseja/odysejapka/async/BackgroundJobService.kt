package odyseja.odysejapka.async

import odyseja.odysejapka.Progress
import odyseja.odysejapka.Status
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class BackgroundJobService {

    private val runners = ConcurrentHashMap<String, ProcessRunner>()

    fun start(jobType: String, runner: Runner) {
        val existing = runners[jobType]
        if (existing?.getProgress()?.status == Status.RUNNING) {
            throw RuntimeException("Job $jobType is already running")
        }
        existing?.shutdown()
        val processRunner = ProcessRunner(runner)
        processRunner.start()
        runners[jobType] = processRunner
    }

    fun stop(jobType: String) {
        runners[jobType]?.stop()
    }

    fun getProgress(jobType: String): Progress {
        return runners[jobType]?.getProgress() ?: Progress(0, Status.STOPPED, listOf())
    }
}
