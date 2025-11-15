package odyseja.odysejapka.users

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {

    fun findByUserId(userId: String): UserEntity?
}