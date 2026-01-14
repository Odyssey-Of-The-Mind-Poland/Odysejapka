package odyseja.odysejapka.form

import org.springframework.data.repository.CrudRepository

interface TeamResultEntryRepository : CrudRepository<TeamResultEntryEntity, Long> {
    fun findByPerformanceEntityIdAndFormEntryEntityIdAndJudgeTypeAndJudge(
        performanceEntityId: Int,
        formEntryEntityId: Long,
        judgeType: JudgeType,
        judge: Int
    ): TeamResultEntryEntity?

    fun findByPerformanceEntityId(performanceId: Int): List<TeamResultEntryEntity>
    fun deleteAllByFormEntryEntity(formEntry: FormEntryEntity)
}
