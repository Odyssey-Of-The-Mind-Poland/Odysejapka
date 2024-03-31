package odyseja.odysejapka.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserDetailsEntity, Int> {
    fun findFirstByUsername(username: String): UserDetailsEntity
}