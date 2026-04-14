package odyseja.odysejapka.timetable

import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AgeService(
    private val ageRepository: AgeRepository,
    private val changeService: ChangeService
) {

    @Transactional
    fun getAge(ageId: Int): AgeEntity {
        return ageRepository.findFirstById(ageId)
            ?: saveAge(AgeEntity(ageId, ageId.toString()))
    }

    @Transactional
    fun saveAge(age: AgeEntity): AgeEntity {
        return ageRepository.save(age).also{
            changeService.updateVersion()
        }
    }
}