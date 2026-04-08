package odyseja.odysejapka.age

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.change.ChangeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AgeService(
    private val ageRepository: AgeRepository,
    private val changeService: ChangeService
) {

    fun getAges(): MutableIterable<AgeEntity?> {
        return ageRepository.findAll()
    }

    fun getAge(ageId: Int): AgeEntity {
        return ageRepository.findFirstById(ageId)
            ?: throw EntityNotFoundException("Nie znaleziono grupy wiekowej $ageId")
    }

    @Transactional
    fun addAge(age: AgeEntity) {
        age.validate()
        ageRepository.save(age)
        changeService.updateVersion()
    }

    @Transactional
    fun ensureAgeExists(ageId: Int) {
        try {
            getAge(ageId)
        } catch (_: EntityNotFoundException) {
            addAge(AgeEntity(ageId, ageId.toString()))
        }
    }

    @Transactional
    fun setUpDefaultAges() {
        arrayOf(0, 1, 2, 3, 4)
            .forEach {ensureAgeExists(it)}
        changeService.updateVersion()
    }

    @Transactional
    fun deleteAge(ageId: Int) {
        ageRepository.deleteById(ageId)
        changeService.updateVersion()
    }
}