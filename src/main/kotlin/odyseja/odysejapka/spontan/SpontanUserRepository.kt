package odyseja.odysejapka.spontan

import org.springframework.data.repository.CrudRepository

interface SpontanUserRepository : CrudRepository<SpontanUserEntity, Long> {
    fun findAllByCityId(cityId: Int): List<SpontanUserEntity>
    fun findByUserId(userId: Long): SpontanUserEntity?
    fun deleteByCityId(cityId: Int)
}
