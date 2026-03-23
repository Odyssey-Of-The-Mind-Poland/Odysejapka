package odyseja.odysejapka.city

import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.form.JudgeCountService
import odyseja.odysejapka.info.InfoService
import odyseja.odysejapka.sponsor.SponsorService
import odyseja.odysejapka.spontan.SpontanGroupAssignmentService
import odyseja.odysejapka.spontan.SpontanUserService
import odyseja.odysejapka.stage.StageService
import odyseja.odysejapka.timetable.TimeTableService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityDeletionService(
    private val cityService: CityService,
    private val cityRepository: CityRepository,
    private val changeService: ChangeService,
    private val timeTableService: TimeTableService,
    private val stageService: StageService,
    private val judgeCountService: JudgeCountService,
    private val infoService: InfoService,
    private val sponsorService: SponsorService,
    private val spontanGroupAssignmentService: SpontanGroupAssignmentService,
    private val spontanUserService: SpontanUserService
) {
    @Transactional
    fun deleteCity(cityId: Int) {

        timeTableService.clearTimetableByCity(cityId)
        stageService.deleteStagesByCity(cityId)
        judgeCountService.deleteJudgeCountsByCity(cityId)
        infoService.deleteInfoByCity(cityId)
        sponsorService.deleteSponsorsByCity(cityId)
        spontanGroupAssignmentService.deleteAssignmentEntitiesByCity(cityId)
        spontanUserService.deleteSpontanUsersByCity(cityId)
        cityRepository.deleteById(cityId)

        changeService.updateVersion()
    }

    @Transactional
    fun clearCities() {
        val cities = cityService.getCities()
        cities.forEach {
            deleteCity(it!!.id)
        }

        changeService.updateVersion()
    }
}