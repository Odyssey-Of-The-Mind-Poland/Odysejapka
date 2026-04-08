package odyseja.odysejapka.timetable

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AgeService(
    private val ageRepository: AgeRepository,
    private val changeService: ChangeService
) {

    fun getAge(ageId: Int): AgeEntity {
        return ageRepository.findFirstById(ageId)
            ?: throw EntityNotFoundException("Nie znaleziono grupy wiekowej $ageId")
    }

    @Transactional
    fun addAge(age: AgeEntity): AgeEntity {
        age.validate()
        val age = ageRepository.save(age)
        changeService.updateVersion()
        return age
    }

    @Transactional
    fun ensureAgeExists(ageId: Int): AgeEntity {
        return try {
            getAge(ageId)
        } catch (_: EntityNotFoundException) {
            addAge(AgeEntity(ageId, ageId.toString()))
        }
    }
}