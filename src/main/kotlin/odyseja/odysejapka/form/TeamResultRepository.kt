package odyseja.odysejapka.form

import org.springframework.data.repository.CrudRepository

interface TeamResultRepository : CrudRepository<TeamResultEntity, Long> {
    fun findByPerformanceId(performanceId: Int): TeamResultEntity?
    fun findAllByPerformanceIdIn(performanceIds: List<Int>): List<TeamResultEntity>
    fun deleteByPerformanceId(performanceId: Int)
    fun deleteAllByPerformanceIdIn(performanceIds: List<Int>)
}

