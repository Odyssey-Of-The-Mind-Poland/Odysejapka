package odyseja.odysejapka.service

import odyseja.odysejapka.domain.Info
import odyseja.odysejapka.domain.InfoEntity
import odyseja.odysejapka.port.InfoUseCase
import org.springframework.stereotype.Service

@Service
internal class InfoService(
  private val infoRepository: InfoRepository,
  private val cityRepository: CityRepository
) : InfoUseCase {

  override fun getInfo(city: Int): Iterable<Info?>? {
    return infoRepository.findByCity(cityRepository.findFirstById(city)).map { it.toInfo() }
  }

  override fun getAllInfo(): List<Info> {
    return infoRepository.findAll().map { it!!.toInfo() }
  }

  override fun addInfo(info: Info): Info {
    infoRepository.save(
      InfoEntity(
        info.id,
        info.infoName,
        info.infoText,
        cityRepository.findFirstById(info.city)
      )
    )
    return info
  }

  override fun updateInfo(info: Info): Info {
    val infoEntity = infoRepository.findById(info.id).get()
    infoEntity.infoText = info.infoText
    infoRepository.save(infoEntity)
    return info
  }
}