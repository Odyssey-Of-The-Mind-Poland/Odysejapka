package odyseja.odysejapka.form

import org.springframework.data.repository.CrudRepository

interface TeamResultRepository : CrudRepository<TeamResultEntity, Long> {
    fun findByPerformanceId(performanceId: Int): TeamResultEntity?
}

