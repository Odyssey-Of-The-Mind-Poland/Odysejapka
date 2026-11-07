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

    fun getAges(): List<Age> {
        val saved = ageRepository.findAll().associateBy { it.id }
        return (0..4).map { id -> Age(id, saved[id]?.icon, saved[id]?.color) }
    }

    @Transactional
    fun updateAges(ages: List<Age>) {
        for (age in ages) {
            val entity = ageRepository.findFirstById(age.id) ?: AgeEntity(age.id, age.id.toString())
            entity.icon = age.icon
            entity.color = age.color
            ageRepository.save(entity)
        }
        changeService.updateVersion()
    }

    @Transactional
    fun saveAge(age: AgeEntity): AgeEntity {
        return ageRepository.save(age).also{
            changeService.updateVersion()
        }
    }
}