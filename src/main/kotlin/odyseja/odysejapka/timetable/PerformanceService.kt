package odyseja.odysejapka.timetable

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PerformanceService (
    private val performanceRepository: PerformanceRepository,
) {
    fun getPerformance(performanceId: Int): Performance {
        return getPerformanceEntity(performanceId).toPerformance()
    }

    fun getPerformanceEntity(performanceId: Int): PerformanceEntity {
        val performance = performanceRepository.findFirstById(performanceId)
            ?: throw EntityNotFoundException("Nie znaleziono przedstawienia o ID $performanceId")
        return performance
    }

    fun getPerformancesByCity(cityId: Int): List<Performance> {
        return getPerformanceEntitiesByCity(cityId).map { it.toPerformance() }
    }

    fun getPerformanceEntitiesByCity(cityId: Int): List<PerformanceEntity> {
        return performanceRepository.findAllByCityEntityId(cityId)
    }

    fun getAllPerformanceEntities(): Iterable<PerformanceEntity?> {
        return performanceRepository.findAll()
    }

    @Transactional
    fun savePerformance(performance: PerformanceEntity): PerformanceEntity {
        return performanceRepository.save(performance)
    }

    @Transactional
    fun savePerformances(performances: List<PerformanceEntity>): List<PerformanceEntity> {
        performanceRepository.saveAll(performances)
        return performances
    }

    @Transactional
    fun deletePerformance(id: Int) {
        if (!performanceRepository.existsById(id))
            throw EntityNotFoundException("Nie znaleziono przedstawienia o ID $id")
        performanceRepository.deleteById(id)
    }

    @Transactional
    fun deleteAllPerformances() {
        performanceRepository.deleteAll()
    }

    @Transactional
    fun deletePerformancesByCity(city: CityEntity) {
        performanceRepository.deleteByCityEntity(city)
    }
}