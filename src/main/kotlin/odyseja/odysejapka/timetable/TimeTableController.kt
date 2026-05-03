package odyseja.odysejapka.timetable

import odyseja.odysejapka.Progress
import odyseja.odysejapka.util.GoogleIdExtractor
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = ["/timeTable", "/api/timeTable"])
class TimeTableController(
    private val timeTableService: TimeTableService,
    private val zspImportService: ZspImportService,
    private val csvImportService: CsvImportService,
    private val performanceGroupService: PerformanceGroupService
    ) {

    @GetMapping
    fun getPerformances(@RequestParam(required = false) cityId: Int?): List<Performance> {
        return cityId?.let { timeTableService.getPerformancesByCity(cityId) } ?: timeTableService.getFinals()
    }

    @GetMapping("/{performanceId}")
    fun getPerformance(@PathVariable performanceId: Int): Performance {
        return timeTableService.getPerformance(performanceId)
    }

    @PostMapping("/import/csv")
    @Secured("ROLE_ADMINISTRATOR")
    fun csvImport(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("cityId") cityId: Int,
    ) {
        csvImportService.uploadCsvFile(file, cityId)
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
    fun deletePerformance(@PathVariable performanceId: Int) {
        timeTableService.deletePerformance(performanceId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping
    fun clearTimetable() {
        timeTableService.clearTimetable()
    }

    @Secured("ROLE_ADMINISTRATOR")
    @DeleteMapping("/{cityId}")
    fun clearTimetableByCity(@PathVariable cityId: Int) {
        timeTableService.clearTimetableByCity(cityId)
    }

    @Secured("ROLE_ADMINISTRATOR")
    @PostMapping("/import/zsp")
    fun zspImport(@RequestBody zspImportRequest: ZspImportRequest, @RequestParam cityId: Int) {
        return zspImportService.import(GoogleIdExtractor.extractGoogleId(zspImportRequest.zspId), cityId)
    }

    @PostMapping("/import/zsp/stop")
    @Secured("ROLE_ADMINISTRATOR")
    fun stopZspImport() {
        zspImportService.stop()
    }

    @GetMapping("/import/zsp/status")
    fun getGadStatus(): Progress {
        return zspImportService.getProgress()
    }

    @GetMapping("/groups")
    fun getPerformanceGroups(@RequestParam(required = false) cityId: Int?): List<PerformanceGroup> {
        return performanceGroupService.getPerformanceGroups(cityId)
    }

    data class ZspImportRequest(
     val zspId: String,
    )
}