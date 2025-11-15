package odyseja.odysejapka.users

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import odyseja.odysejapka.roles.Role

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRolesRepository: UserRolesRepository
) {

    @Transactional
    fun createUser(user: CreateUserRequest): User {
        val entity = userRepository.save(UserEntity.from(user))
        if (isFirstUser()) {
            assignRolesToUser(
                UserRoles(
                    userId = entity.id ?: throw IllegalStateException("User ID cannot be null"),
                    roles = listOf(Role.ADMINISTRATOR)
                )
            )
        }
        return entity.toUser()
    }

    private fun isFirstUser(): Boolean = userRepository.findAll().count() == 1

    @Transactional(readOnly = true)
    fun getUserByUserId(userId: String): User? {
        val entity = userRepository.findByUserId(userId)
        if (entity != null) {
            val roles = userRolesRepository.findByUserId(userId).map { it.toUserRole() }
            return entity.toUser(roles)
        }
        return null
    }

    @Transactional(readOnly = true)
    fun getUser(id: Long): User? {
        val entity = userRepository.findById(id)
        return entity.map { userEntity ->
            val userId = userEntity.userId ?: return@map null
            val roles = userRolesRepository.findByUserId(userId).map { it.toUserRole() }
            userEntity.toUser(roles)
        }.orElseThrow { EntityNotFoundException("User with ID $id not found") }
    }

    @Transactional(readOnly = true)
    fun listUsers(): List<User> {
        return userRepository.findAll().map { entity ->
            val roles =
                entity.userId?.let { userRolesRepository.findByUserId(it).map { it.toUserRole() } } ?: emptyList()
            entity.toUser(roles)
        }
    }

    @Transactional
    fun assignRolesToUser(userRoles: UserRoles): UserRoles {
        val user = userRepository.findById(userRoles.userId)
            .orElseThrow { EntityNotFoundException("User with ID ${userRoles.userId} not found") }
        userRolesRepository.deleteByUserId(user.userId!!)

        val roleEntities = UserRolesEntity.from(userRoles, user.userId!!)
        userRolesRepository.saveAll(roleEntities)

        return userRoles
    }

    @Transactional(readOnly = true)
    fun getUserRoles(userId: Long): UserRoles {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User with ID ${userId} not found") }
        val roleEntities = userRolesRepository.findByUserId(user.userId!!)
        val roles = roleEntities.map { it.toUserRole() }
        return UserRoles(userId, roles)
    }
}
