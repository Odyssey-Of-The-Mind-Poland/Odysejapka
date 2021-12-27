package odyseja.odysejapka.service

import odyseja.odysejapka.domain.InfoEntity
import odyseja.odysejapka.port.InfoUseCase
import org.springframework.stereotype.Service

@Service
internal class InfoService(private val infoRepository: InfoRepository) : InfoUseCase {

  override fun getInfo(city: String): Iterable<InfoEntity?>? {
    return infoRepository.findByCity(city)
  }

  override fun getAllInfo(): MutableIterable<InfoEntity?> {
    return infoRepository.findAll()
  }

  override fun addInfo(infoEntity: List<InfoEntity>): List<InfoEntity> {
    infoRepository.saveAll(infoEntity)
    return infoEntity
  }
}