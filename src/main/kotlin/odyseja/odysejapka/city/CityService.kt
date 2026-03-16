package odyseja.odysejapka.city

import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.exceptions.CityNotFoundException
import odyseja.odysejapka.form.CityFormJudgesRepository
import odyseja.odysejapka.info.InfoRepository
import odyseja.odysejapka.sponsor.SponsorRepository
import odyseja.odysejapka.spontan.SpontanGroupAssignmentRepository
import odyseja.odysejapka.spontan.SpontanUserRepository
import odyseja.odysejapka.stage.StageRepository
import odyseja.odysejapka.timetable.PerformanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityService(
  private val cityRepository: CityRepository,
  private val changeService: ChangeService,
  private val performanceRepository: PerformanceRepository,
  private val stageRepository: StageRepository,
  private val cityFormJudgesRepository: CityFormJudgesRepository,
  private val infoRepository: InfoRepository,
  private val sponsorRepository: SponsorRepository,
  private val spontanGroupAssignmentRepository: SpontanGroupAssignmentRepository,
  private val spontanUserRepository: SpontanUserRepository
) {

  fun getCities(): MutableIterable<CityEntity?> {
    return cityRepository.findAll()
  }

  @Transactional
  fun addCity(city: CreateCityRequest): CityEntity {
    val saved = cityRepository.save(CityEntity(0, city.name))
    changeService.updateVersion()
    return saved
  }

  @Transactional
  fun deleteCity(cityId: Int) {

    val city = cityRepository.findFirstById(cityId) ?: throw CityNotFoundException(cityId = cityId)

    performanceRepository.deleteByCityEntity(city)
    stageRepository.deleteByCityEntity(city)
    cityFormJudgesRepository.deleteByCity(city)
    infoRepository.deleteByCityId(cityId)
    sponsorRepository.deleteByCityId(cityId)
    spontanGroupAssignmentRepository.deleteByCityId(cityId)
    spontanUserRepository.deleteByCityId(cityId)
    cityRepository.deleteById(cityId)

    changeService.updateVersion()
  }

  @Transactional
  fun clearCities() {
    val cities = getCities()
    cities.forEach {
      deleteCity(it!!.id)
    }

    changeService.updateVersion()
  }

  fun getCityByName(cityName: String): CityEntity {
    return cityRepository.findFirstByName(cityName) ?: throw CityNotFoundException(cityName = cityName)
  }
}