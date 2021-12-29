package odyseja.odysejapka.service

import odyseja.odysejapka.domain.AgeEntity
import odyseja.odysejapka.port.UserUseCase
import org.springframework.stereotype.Service

@Service
internal class AgeService(private val ageRepository: AgeRepository) : UserUseCase {

  override fun getAge(): MutableIterable<AgeEntity?> {
    return ageRepository.findAll()
  }

  override fun updateAge(ageEntities: List<AgeEntity>) {
    for (age in ageEntities){
      val toEdit: AgeEntity = ageRepository.findById(age.id).get()
      toEdit.name = age.name
      ageRepository.save(toEdit)
    }
  }
}