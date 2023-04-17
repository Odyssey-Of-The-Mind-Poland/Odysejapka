package odyseja.odysejapka.service

import odyseja.odysejapka.domain.AgeEntity
import org.springframework.stereotype.Service

@Service
class AgeService(
  private val ageRepository: AgeRepository,
  private val changeService: ChangeService
) {

  fun getAge(): MutableIterable<AgeEntity?> {
    return ageRepository.findAll()
  }

  fun updateAge(ageEntities: List<AgeEntity>) {
    for (age in ageEntities) {
      val toEdit: AgeEntity = ageRepository.findById(age.id).get()
      toEdit.name = age.name
      ageRepository.save(toEdit)
    }

    changeService.updateVersion()
  }

  fun deleteAge(ageId: Int) {
    ageRepository.deleteById(ageId)

    changeService.updateVersion()
  }
}