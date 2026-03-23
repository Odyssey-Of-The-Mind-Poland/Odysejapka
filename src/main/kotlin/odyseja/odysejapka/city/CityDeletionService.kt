package odyseja.odysejapka.city

import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.form.JudgeCountService
import odyseja.odysejapka.info.InfoRepository
import odyseja.odysejapka.sponsor.SponsorService
import odyseja.odysejapka.spontan.SpontanGroupAssignmentService
import odyseja.odysejapka.spontan.SpontanUserRepository
import odyseja.odysejapka.stage.StageRepository
import odyseja.odysejapka.timetable.TimeTableService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityDeletionService(
    private val cityService: CityService,
    private val cityRepository: CityRepository,
    private val changeService: ChangeService,
    private val timeTableService: TimeTableService,
    private val stageRepository: StageRepository,
    private val judgeCountService: JudgeCountService,
    private val infoRepository: InfoRepository,
    private val sponsorService: SponsorService,
    private val spontanGroupAssignmentService: SpontanGroupAssignmentService,
    private val spontanUserRepository: SpontanUserRepository
) {
    @Transactional
    fun deleteCity(cityId: Int) {

        val city = cityService.getCity(cityId)

        timeTableService.clearTimetableByCity(cityId)
        stageRepository.deleteByCityEntity(city)
        judgeCountService.deleteJudgeCountsByCity(cityId)
        infoRepository.deleteByCityId(cityId)
        sponsorService.deleteSponsorsByCity(cityId)
        spontanGroupAssignmentService.deleteAssignmentEntitiesByCity(cityId)
        spontanUserRepository.deleteByCityId(cityId)
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