package odyseja.odysejapka.form

import org.springframework.data.repository.CrudRepository

interface TeamResultEntryRepository : CrudRepository<TeamResultEntryEntity, Long> {
    fun findByPerformanceEntityIdAndFormEntryEntityIdAndJudge(
        performanceEntityId: Int,
        formEntryEntityId: Long,
        judge: Int
    ): TeamResultEntryEntity?

    fun findByPerformanceEntityId(performanceId: Int): List<TeamResultEntryEntity>
    fun deleteAllByFormEntryEntity(formEntry: FormEntryEntity)
}
