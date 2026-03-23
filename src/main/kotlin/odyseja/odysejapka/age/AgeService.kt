package odyseja.odysejapka.age

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@Service
class AgeService(
  private val ageRepository: AgeRepository,
  private val changeService: ChangeService
) {

  fun getAges(): MutableIterable<AgeEntity?> {
    return ageRepository.findAll()
  }

  fun getAge(ageId: Int): AgeEntity {
    return ageRepository.findFirstById(ageId) ?: throw EntityNotFoundException("Nie znaleziono grupy wiekowej $ageId")
  }

  @Transactional
  fun updateAges(ageEntities: List<AgeEntity>) {
    for (age in ageEntities) {
      val toEdit: AgeEntity = ageRepository.findById(age.id).getOrElse { AgeEntity(age.id, age.name) }
      toEdit.name = age.name
      ageRepository.save(toEdit)
    }

    changeService.updateVersion()
  }

  @Transactional
  fun deleteAge(ageId: Int) {
    ageRepository.deleteById(ageId)
    changeService.updateVersion()
  }
}