package odyseja.odysejapka.info

import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InfoService(
  private val infoRepository: InfoRepository,
  private val cityService: CityService,
  private val infoCategoryRepository: InfoCategoryRepository,
  private val changeService: ChangeService
) {

  fun getInfo(cityId: Int): Iterable<Info?>? {
    return infoRepository.findByCity(cityService.getCity(cityId)
    ).map { it.toInfo() }
      .sortedByDescending { it.sortNumber }
  }

  fun getAllInfo(): List<Info> {
    return infoRepository.findAllByOrderBySortNumber().map { it.toInfo() }.sortedByDescending { it.sortNumber }
  }

  fun getInfoCategory(): MutableIterable<InfoCategoryEntity> {
    return infoCategoryRepository.findAll()
  }

  @Transactional
  fun addInfo(info: Info): Info {
    val savedInfo = infoRepository.save(
      InfoEntity(
        info.id,
        info.infoName,
        info.infoText,
        cityService.getCity(info.city),
        infoCategoryRepository.findFirstById(info.category),
        info.sortNumber,
        info.icon,
        info.color
      )
    )

    changeService.updateVersion()

    return savedInfo.toInfo()
  }

  @Transactional
  fun updateInfo(info: Info): Info {
    val infoEntity = infoRepository.findById(info.id).get()
    infoEntity.infoText = info.infoText
    infoEntity.sortNumber = info.sortNumber
    infoEntity.icon = info.icon
    infoEntity.color = info.color
    infoRepository.save(infoEntity)

    changeService.updateVersion()

    return info
  }

  @Transactional
  fun deleteInfo(id: Int) {
    infoRepository.deleteById(id)
    changeService.updateVersion()
  }

  @Transactional
  fun deleteInfoByCity(cityId: Int) {
    infoRepository.deleteByCityId(cityId)
    changeService.updateVersion()
  }

  fun getInfoById(info: Int): Info {
    return infoRepository.findById(info).get().toInfo()
  }
}