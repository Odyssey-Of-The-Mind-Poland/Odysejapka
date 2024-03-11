package odyseja.odysejapka.sak

import SakConfiguration
import odyseja.odysejapka.domain.Progress
import odyseja.odysejapka.domain.Status
import odyseja.odysejapka.timetable.TimeTableService
import org.springframework.stereotype.Service

@Service
class SakService(
    private val timetableService: TimeTableService
) {

    private var sak: SakRunner? = null
    private var job: Thread? = null

    fun runGad(generateSakCommand: GenerateSakCommand) {
        start(generateSakCommand)
    }

    fun stop() {
        job?.stop()
        sak = null
    }

    private fun start(generateSakCommand: GenerateSakCommand) {
        if (sak != null || job?.isAlive == true) {
            throw RuntimeException("Gad is already running")
        }
        sak = SakConfiguration(generateSakCommand.templatesFolderId, timetableService).sakRunner()
        job = Thread {
            sak?.startSak()
        }
        job?.start()
    }

    fun getProgress(): Progress {
        val progress = sak?.getProgress() ?: 100
        return if (progress != 100) {
            Progress(progress, Status.RUNNING)
        } else {
            Progress(100, Status.STOPPED)
        }
    }
}