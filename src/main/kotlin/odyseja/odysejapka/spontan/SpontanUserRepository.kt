package odyseja.odysejapka.spontan

import org.springframework.data.repository.CrudRepository

interface SpontanUserRepository : CrudRepository<SpontanUserEntity, Long> {
    fun findFirstById(userId: Long): SpontanUserEntity?
    fun findAllByCityId(cityId: Int): List<SpontanUserEntity>
    fun findByUserId(userId: Long): SpontanUserEntity?
}
