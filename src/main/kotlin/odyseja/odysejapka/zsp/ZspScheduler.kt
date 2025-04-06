package odyseja.odysejapka.zsp

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@EnableScheduling
class ZspScheduler(private val zspService: ZspService) {

//    @Scheduled(fixedRate = 300000)
    fun scheduleTeamsFetch() {
        println("refetching teams")
        zspService.refreshTeams()
    }
} 