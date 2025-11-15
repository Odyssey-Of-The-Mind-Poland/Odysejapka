package odyseja.odysejapka.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRolesRepository : CrudRepository<UserRolesEntity, Long> {

    fun findByUserId(userId: String): List<UserRolesEntity>

    fun deleteByUserId(userId: String)
} 