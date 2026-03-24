package odyseja.odysejapka.users

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findFirstById(id: Long): UserEntity?
    fun findByUserId(userId: String): UserEntity?
    fun findByEmail(email: String): UserEntity?
}