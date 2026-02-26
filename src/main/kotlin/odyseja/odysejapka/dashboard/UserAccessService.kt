package odyseja.odysejapka.dashboard

import odyseja.odysejapka.roles.Role
import odyseja.odysejapka.users.UserRepository
import odyseja.odysejapka.users.UserRolesRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAccessService(
    private val userRepository: UserRepository,
    private val userRolesRepository: UserRolesRepository
) {

    @Transactional(readOnly = true)
    fun isAdmin(principalUserId: String): Boolean {
        val user = userRepository.findByUserId(principalUserId) ?: return false
        return userRolesRepository.findByUserId(user.userId!!).any { it.role == Role.ADMINISTRATOR }
    }
}
