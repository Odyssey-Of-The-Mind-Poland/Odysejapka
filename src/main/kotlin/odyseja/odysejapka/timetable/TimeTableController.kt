package odyseja.odysejapka.timetable

import odyseja.odysejapka.Progress
import odyseja.odysejapka.util.GoogleIdExtractor
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = ["/timeTable", "/api/timeTable"])
class TimeTableController(
    private val timeTableService: TimeTableService,
    private val importTimetableService: ImportTimetableService,
    private val importCsvService: ImportCsvService
) {

    @GetMapping
    fun getPerformances(@RequestParam(required = false) cityId: Int?): List<Performance> {
        return cityId?.let { timeTableService.getByCity(cityId) } ?: timeTableService.getFinals()
    }

    @GetMapping("/{performanceId}")
    fun getPerformance(@PathVariable performanceId: Int): Performance {
        return timeTableService.getPerformance(performanceId)
    }

    @PostMapping("/csv")
    @Secured("ROLE_ADMINISTRATOR")
    fun importPerformances(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("cityId") cityId: Int,
    ) {
        importCsvService.uploadCsvFile(file, cityId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping
    @ResponseBody
    fun addPerformance(@RequestBody performance: Performance): PerformanceEntity {
        return timeTableService.addPerformance(performance)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PutMapping
    fun updatePerformance(@RequestBody performance: Performance) {
        timeTableService.updatePerformance(performance)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping("/{performanceId}")
    fun delPerformance(@PathVariable performanceId: Int) {
        timeTableService.delPerformance(performanceId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/import")
    fun import(@RequestBody importTimeTable: ImportTimeTable, @RequestParam cityId: Int) {
        return importTimetableService.import(GoogleIdExtractor.extractGoogleId(importTimeTable.zspId), cityId)
    }

    @PostMapping("/import/stop")
    @Secured("ROLE_ADMINISTRATOR")
    fun stopImport() {
        importTimetableService.stop()
    }

    @GetMapping("/import/status")
    fun getGadStatus(): Progress {
        return importTimetableService.getProgress()
    }

    data class ImportTimeTable(
     val zspId: String,
    )
}