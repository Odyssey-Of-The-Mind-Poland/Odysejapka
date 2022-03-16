package odyseja.odysejapka.service

import odyseja.odysejapka.domain.AgeEntity
import odyseja.odysejapka.port.AgeUseCase
import odyseja.odysejapka.port.ChangeUseCase
import org.springframework.stereotype.Service

@Service
internal class AgeService(
  private val ageRepository: AgeRepository,
  private val changeUseCase: ChangeUseCase
) : AgeUseCase {

  override fun getAge(): MutableIterable<AgeEntity?> {
    return ageRepository.findAll()
  }

  override fun updateAge(ageEntities: List<AgeEntity>) {
    for (age in ageEntities) {
      val toEdit: AgeEntity = ageRepository.findById(age.id).get()
      toEdit.name = age.name
      ageRepository.save(toEdit)
    }

    changeUseCase.updateVersion()
  }

  override fun deleteAge(ageId: Int) {
    ageRepository.deleteById(ageId)

    changeUseCase.updateVersion()
  }
}