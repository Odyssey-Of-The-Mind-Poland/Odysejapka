package odyseja.odysejapka.spontan

import org.springframework.data.repository.CrudRepository

interface SpontanResultRepository : CrudRepository<SpontanResultEntity, Long> {
    fun findByPerformanceId(performanceId: Int): SpontanResultEntity?
    fun findAllByPerformanceIdIn(performanceIds: List<Int>): List<SpontanResultEntity>
}
