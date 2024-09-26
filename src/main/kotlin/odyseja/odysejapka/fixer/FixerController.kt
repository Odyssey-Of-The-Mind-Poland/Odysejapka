package odyseja.odysejapka.fixer

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/fixer")
class FixerController(private val fixerService: FixerService) {
    @PostMapping("/start")
    fun startFixing(@RequestBody fixSheetsCommand: FixSheetsCommand) {
        fixerService.start(fixSheetsCommand)
    }

    @PostMapping("/stop")
    fun startFixing() {
        fixerService.stop()
    }
}