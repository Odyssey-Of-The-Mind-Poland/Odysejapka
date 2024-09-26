package odyseja.odysejapka.fixer

import odyseja.odysejapka.async.ProcessRunner
import org.springframework.stereotype.Service

@Service
class FixerService {

    private var runner: ProcessRunner? = null

    fun start(fixSheetsCommand: FixSheetsCommand) {
        runner = ProcessRunner(
            FixerConfiguration(
                fixSheetsCommand.folderId,
                fixSheetsCommand.pattern,
                fixSheetsCommand.cells
            ).fixerRunner()
        )
        runner?.start()
    }

    fun stop() {
        runner?.stop()
    }
}