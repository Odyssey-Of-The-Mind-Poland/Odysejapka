package odyseja.odysejapka.async

import odyseja.odysejapka.Progress
import odyseja.odysejapka.Status
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class ProcessRunner(private val runner: Runner) {

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val jobRef = AtomicReference<Future<*>?>(null)
    private val failureRef = AtomicReference<Throwable?>(null)
    private val cancelRequested = AtomicBoolean(false)

    fun start() {
        if (jobRef.get()?.let { !it.isDone } == true) {
            throw RuntimeException("Runner is already working")
        }
        failureRef.set(null)
        cancelRequested.set(false)
        val future = executor.submit {
            try {
                runner.run()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                if (!cancelRequested.get()) {
                    failureRef.set(e)
                }
            } catch (e: Throwable) {
                failureRef.set(e)
            }
        }
        jobRef.set(future)
    }

    fun stop() {
        cancelRequested.set(true)
        val cancellable = runner as? CancellableRunner
        if (cancellable != null) {
            cancellable.requestCancel()
        }
        jobRef.get()?.cancel(true)
    }

    fun getProgress(): Progress {
        val job = jobRef.get()
        val failure = failureRef.get()
        if (failure != null) {
            return Progress(runner.getProgress(), Status.FAILED, getLogs())
        }
        if (job?.isDone == true) {
            val status = if (cancelRequested.get()) Status.CANCELLED else Status.STOPPED
            return Progress(runner.getProgress(), status, getLogs())
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

    fun shutdown() {
        executor.shutdownNow()
    }
}
