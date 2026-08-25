package odyseja.odysejapka.stage

import org.springframework.data.repository.CrudRepository

interface StageUserRepository : CrudRepository<StageUserEntity, Long> {
    fun findByCityIdAndStage(cityId: Int, stage: Int): StageUserEntity?
    fun deleteAllByCityId(cityId: Int)
    fun findByUserId(userId: Long): StageUserEntity?
}