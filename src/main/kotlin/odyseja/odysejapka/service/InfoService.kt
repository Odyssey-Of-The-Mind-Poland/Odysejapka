package odyseja.odysejapka.service

import odyseja.odysejapka.domain.Info
import odyseja.odysejapka.domain.InfoCategoryEntity
import odyseja.odysejapka.domain.InfoEntity
import org.springframework.stereotype.Service

@Service
class InfoService(
  private val infoRepository: InfoRepository,
  private val cityRepository: CityRepository,
  private val infoCategoryRepository: InfoCategoryRepository,
  private val changeService: ChangeService
) {

  fun getInfo(city: Int): Iterable<Info?>? {
    return infoRepository.findByCity(cityRepository.findFirstById(city)).map { it.toInfo() }.sortedBy { it.sortNumber }
  }

  fun getAllInfo(): List<Info> {
    return infoRepository.findAllByOrderBySortNumber().map { it.toInfo() }.sortedBy { it.sortNumber }
  }

  fun getInfoCategory(): MutableIterable<InfoCategoryEntity> {
    return infoCategoryRepository.findAll()
  }


  fun addInfo(info: Info): Info {
    val savedInfo = infoRepository.save(
      InfoEntity(
        info.id,
        info.infoName,
        info.infoText,
        cityRepository.findFirstById(info.city),
        infoCategoryRepository.findFirstById(info.category),
        info.sortNumber
      )
    )

    changeService.updateVersion()

    return savedInfo.toInfo()
  }

  fun updateInfo(info: Info): Info {
    val infoEntity = infoRepository.findById(info.id).get()
    infoEntity.infoText = info.infoText
    infoEntity.sortNumber = info.sortNumber
    infoRepository.save(infoEntity)

    changeService.updateVersion()

    return info
  }

  fun deleteInfo(id: Int) {

    changeService.updateVersion()

    infoRepository.deleteById(id)
  }

  fun getInfoById(info: Int): Info {
    return infoRepository.findById(info).get().toInfo()
  }
}